let stompClient = null;

let currentUsername = null;
let currentUserId = null;

let currentChat = { type: "public", withUserId: null, withUsername: null, pendingLoad: false };

const loginOverlay = document.getElementById('loginOverlay');
const messagePage = document.getElementById('container');
const usernameInput = document.getElementById('input-username');
const usernameForm = document.getElementById('userForm');

const usersDiv = document.getElementById('users');
const messagesDiv = document.getElementById('messages');
const inputMsg = document.getElementById('input-msg');
const sendBtn = document.getElementById('send-msg-btn');

const chatWithEl = document.getElementById('chat-with');
const publicBtn = document.getElementById('public-chat-btn');

messagePage.classList.add('blurred');

let privateSubscribed = false;

const userOrder = [];                 // array of userIds in current display order
const usersById = new Map();          // userId -> {id, username}
const unreadById = new Map();         // userId -> count

const MAX_ON_OPEN = 10;

function setChatWith(text) {
  if (chatWithEl) chatWithEl.textContent = text;
}

function clearMessages() {
  messagesDiv.innerHTML = "";
}

function ensurePrivateSubscription() {
  if (privateSubscribed) return;
  if (currentUserId == null) return; // allow 0

  privateSubscribed = true;
  stompClient.subscribe('/topic/private.' + currentUserId, payload =>
    onPrivateMessage(JSON.parse(payload.body))
  );
}

function moveUserToTop(userId) {
  const idStr = String(userId);
  const el = usersDiv.querySelector(`.user-container[data-user-id="${idStr}"]`);
  if (el) usersDiv.prepend(el);

  const idx = userOrder.indexOf(userId);
  if (idx > -1) userOrder.splice(idx, 1);
  userOrder.unshift(userId);
}

function setCounter(userId, count) {
  unreadById.set(userId, count);

  const container = usersDiv.querySelector(`.user-container[data-user-id="${String(userId)}"]`);
  if (!container) return;

  const badge = container.querySelector('.new-message-counter');
  if (!badge) return;

  if (count > 0) {
    badge.textContent = String(count);
    badge.classList.remove('hidden');
  } else {
    badge.textContent = "";
    badge.classList.add('hidden');
  }
}

function incrementCounter(userId) {
  const cur = unreadById.get(userId) ?? 0;
  setCounter(userId, cur + 1);
}

function resetCounter(userId) {
  setCounter(userId, 0);
}

function setActiveUser(userId) {
  document.querySelectorAll("#users .user").forEach(el => {
    el.classList.toggle("active", userId != null && el.dataset.userId === String(userId));
  });
}

usersDiv.addEventListener('click', (e) => {
  const container = e.target.closest('.user-container');
  if (!container) return;

  const id = Number(container.dataset.userId);
  const name = container.dataset.username;
  if (Number.isNaN(id) || name == null) return;

  openPrivateChat(id, name);
}, true);

usersDiv.addEventListener('keydown', (e) => {
  if (e.key !== 'Enter' && e.key !== ' ') return;
  const container = e.target.closest('.user-container');
  if (!container) return;
  e.preventDefault();

  const id = Number(container.dataset.userId);
  const name = container.dataset.username;
  if (Number.isNaN(id) || name == null) return;

  openPrivateChat(id, name);
}, true);

function connect(event) {
  event.preventDefault();
  document.getElementById('send-username-btn').disabled = true;

  currentUsername = (usernameInput.value || "").trim();
  if (!currentUsername) return;

  const socket = new SockJS('http://' + document.location.host + '/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  stompClient.subscribe('/topic/public', payload => onPublicMessage(JSON.parse(payload.body)));
  stompClient.subscribe('/topic/users', payload => onUsersUpdate(JSON.parse(payload.body)));

  stompClient.send('/app/chat.registerUser', {}, JSON.stringify({ username: currentUsername }));

  loginOverlay.classList.add('hidden');
  messagePage.classList.remove('blurred');

  openPublicChat();
}

function onError(err) {
  console.log("WebSocket error:", err);
}

function onUsersUpdate(users) {
  // learn my id from list
  const me = (users || []).find(u => u && u.username === currentUsername);
  if (me && me.id != null) currentUserId = me.id;

  ensurePrivateSubscription();

  if (currentChat.type === "private" && currentChat.pendingLoad && currentUserId != null) {
    currentChat.pendingLoad = false;
    loadPrivateMessages(currentChat.withUserId);
  }

  renderUsers(users);
}

function renderUsers(users) {
  const incoming = (users || [])
    .filter(u => u && u.id != null && u.username)
    .filter(u => u.username !== currentUsername);

  usersById.clear();
  incoming.forEach(u => usersById.set(u.id, { id: u.id, username: u.username }));

  const incomingIds = new Set(incoming.map(u => u.id));
  for (let i = userOrder.length - 1; i >= 0; i--) {
    if (!incomingIds.has(userOrder[i])) userOrder.splice(i, 1);
  }
  incoming.forEach(u => {
    if (!userOrder.includes(u.id)) userOrder.push(u.id);
  });

  usersDiv.innerHTML = '';
  userOrder.forEach(id => {
    const u = usersById.get(id);
    if (!u) return;

    const container = document.createElement('div');
    container.className = 'user-container';
    container.tabIndex = 0; // keyboard focus
    container.dataset.userId = String(u.id);
    container.dataset.username = u.username;

    const userEl = document.createElement('div');
    userEl.className = 'user';
    userEl.dataset.userId = String(u.id);
    userEl.dataset.username = u.username;
    userEl.textContent = u.username;

    const badge = document.createElement('span');
    badge.className = 'new-message-counter hidden';

    container.appendChild(userEl);
    container.appendChild(badge);

    usersDiv.appendChild(container);

    const count = unreadById.get(u.id) ?? 0;
    if (count > 0) {
      badge.textContent = String(count);
      badge.classList.remove('hidden');
    }
  });

  setActiveUser(currentChat.type === "private" ? currentChat.withUserId : null);
}

function openPublicChat() {
  currentChat = { type: "public", withUserId: null, withUsername: null, pendingLoad: false };
  setChatWith("Public chat");
  clearMessages();
  setActiveUser(null);
  setPublicBtnActive(true);
  loadPublicMessages();
}

function openPrivateChat(withUserId, withUsername) {
  if (withUserId == null) return;

  currentChat = {
    type: "private",
    withUserId,
    withUsername,
    pendingLoad: (currentUserId == null)
  };

  setChatWith(withUsername);
  clearMessages();
  setActiveUser(withUserId);
  setPublicBtnActive(false);

  resetCounter(withUserId);

  if (currentUserId != null) {
    currentChat.pendingLoad = false;
    loadPrivateMessages(withUserId);
  }
}

if (publicBtn) {
  publicBtn.addEventListener("click", () => {
    if (currentChat.type === "private") openPublicChat();
  });

  publicBtn.addEventListener("keydown", (e) => {
    if ((e.key === "Enter" || e.key === " ") && currentChat.type === "private") {
      e.preventDefault();
      openPublicChat();
    }
  });
}

function setPublicBtnActive(isActive){
  if (!publicBtn) return;
  publicBtn.classList.toggle("active", !!isActive);
}

function sendMessage(event) {
  if (event) event.preventDefault();
  const text = (inputMsg.value || "").trim();
  if (!text || !stompClient) return;

  if (currentChat.type === "public") {
    stompClient.send('/app/chat.sendPublic', {}, JSON.stringify({ content: text }));
  } else {
    if (currentChat.withUserId == null) return;

    stompClient.send('/app/chat.sendPrivate', {}, JSON.stringify({
      content: text,
      toUserId: currentChat.withUserId
    }));

    moveUserToTop(currentChat.withUserId);
  }

  inputMsg.value = "";
}

sendBtn.addEventListener("click", sendMessage);

document.addEventListener("keydown", ev => {
  if (ev.key === "Enter" && document.activeElement === inputMsg) {
    sendMessage(ev);
  }
});

function onPublicMessage(msg) {
  if (currentChat.type !== "public") return;
  addMessage(msg);
}

function onPrivateMessage(msg) {
  const senderId = msg?.sender?.id;
  const receiverId = msg?.receiver?.id;
  if (senderId == null || receiverId == null) return;

  if (currentUserId == null) return;
  if (senderId !== currentUserId && receiverId !== currentUserId) return;

  const otherUserId = (senderId === currentUserId) ? receiverId : senderId;

  moveUserToTop(otherUserId);

  const isViewingThisChat =
    currentChat.type === "private" &&
    currentChat.withUserId === otherUserId;

  if (!isViewingThisChat) {
    if (senderId !== currentUserId) incrementCounter(otherUserId);
    return;
  }

  addMessage(msg);
}

function loadPublicMessages() {
  fetch("/messages/public")
    .then(r => r.json())
    .then(list => {
      const arr = Array.isArray(list) ? list : [];
      const last = arr.slice(-MAX_ON_OPEN);
      last.forEach(addMessage);
    })
    .catch(console.log);
}

function loadPrivateMessages(withUserId) {
  if (currentUserId == null) return;

  fetch("/messages/private?with=" + encodeURIComponent(withUserId), {
    headers: { "X-UserId": String(currentUserId) }
  })
    .then(r => r.json())
    .then(list => {
      const arr = Array.isArray(list) ? list : [];
      const last = arr.slice(-MAX_ON_OPEN);
      last.forEach(addMessage);
    })
    .catch(console.log);
}

function addMessage(messageObj) {
  if (!messageObj) return;

  const content = (messageObj.content ?? "").trim();
  if (!content) return;

  const newNode = document.createElement("div");
  newNode.classList.add("message-container");

  const messageDataNode = document.createElement("div");
  messageDataNode.classList.add("message-data");

  const senderName = messageObj.sender?.username ?? messageObj.username ?? "Unknown";

  const spanUser = document.createElement("span");
  spanUser.classList.add("sender");
  spanUser.textContent = senderName;

  const spanTime = document.createElement("span");
  spanTime.classList.add("date");
  spanTime.textContent = messageObj.timestamp ?? "";

  messageDataNode.append(spanUser, spanTime);

  const spanMessageValue = document.createElement("span");
  spanMessageValue.classList.add("message");
  spanMessageValue.textContent = content;

  newNode.append(messageDataNode, spanMessageValue);

  if (document.querySelector(".message") != null) {
    messagesDiv.insertAdjacentHTML("beforeend", "<hr>");
  }
  messagesDiv.appendChild(newNode);
  newNode.scrollIntoView({ block: "end", behavior: "smooth" });
}

function setup() {
  usernameForm.addEventListener('submit', connect, true);
}

setup();
