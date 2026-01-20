let stompClient = null;
let currentUsername = null;

const loginPage = document.getElementById('loginOverlay');
const messagePage = document.getElementById('container');
const usernameInput = document.getElementById('input-username');
const usernameForm = document.getElementById('userForm');

messagePage.classList.add('blurred');

function connect(event) {
    event.preventDefault();
    document.getElementById('send-username-btn').disabled = true;

    let socket = new SockJS('http://'+ document.location.host +'/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
    event.preventDefault();
}

function onConnected() {
    currentUsername = usernameInput.value.trim();
    stompClient.subscribe('/topic/public', payload => {
        addMessage(JSON.parse(payload.body));
    });
    stompClient.subscribe('/topic/users', payload => {
        renderUsers(JSON.parse(payload.body));
    });

    stompClient.send(
        '/app/chat.registerUser',
        {},
        JSON.stringify({username: currentUsername})
    );

    loginOverlay.classList.add('hidden');
    messagePage.classList.remove('blurred');

    getMessages();
}

function renderUsers(users){
  const usersDiv = document.getElementById('users');
  usersDiv.innerHTML = '';

  const seen = new Set();
  users.forEach(u => {
    const name = u.username ?? u;

    if (!name || seen.has(name)) return;
    seen.add(name);

    if (currentUsername && name === currentUsername) return;

    const el = document.createElement('div');
    el.className = 'user';
    el.textContent = name;
    usersDiv.appendChild(el);
  });
}

function onError(event) {
    console.log(event);
    console.log("onError");
}

function sendMessage(event) {
    let messageContent = document.getElementById("input-msg").value.trim();
    if(messageContent && stompClient) {
        let chatMessage = {
            content: messageContent
        };
        stompClient.send(
            '/app/chat.sendMessage',
            {},
            JSON.stringify(chatMessage)
        );
        document.getElementById("input-msg").value = "";
    }
    event.preventDefault();
}

function addMessage(messageObj) {
    if (!messageObj) return;

    // Handle JOIN/LEAVE system events
    const type = (messageObj.type || "").toLowerCase();
    if (type === "leave" || type === "join") {
        const sys = document.createElement("div");
        sys.className = "message-container";
        sys.innerHTML = `<div class="message-data">
        <span class="sender">System</span>
        <span class="date">${messageObj.timestamp ?? ""}</span>
        </div>
        <span class="message">${messageObj.username ?? "Someone"} ${type === "join" ? "joined" : "left"}</span>`;
        document.getElementById("messages").appendChild(sys);
        sys.scrollIntoView({ block: "end", behavior: "smooth" });
        return;
    }

    // Normal chat message
    const content = (messageObj.content ?? "").trim();
    if (!content) return;

    const newNode = document.createElement("div");
    newNode.classList.add("message-container");

    const messageDataNode = document.createElement("div");
    messageDataNode.classList.add("message-data");

    const spanUser = document.createElement("span");
    spanUser.classList.add("sender");
    spanUser.textContent = messageObj.username ?? messageObj.user ?? "Unknown";

    const spanTime = document.createElement("span");
    spanTime.classList.add("date");
    spanTime.textContent = messageObj.timestamp ?? "";

    messageDataNode.append(spanUser, spanTime);

    const spanMessageValue = document.createElement("span");
    spanMessageValue.classList.add("message");
    spanMessageValue.textContent = content;

    newNode.append(messageDataNode, spanMessageValue);

    const messageContainer = document.getElementById("messages");
    if (document.querySelector(".message") != null) {
        messageContainer.insertAdjacentHTML("beforeend", "<hr>");
    }
    messageContainer.appendChild(newNode);
    newNode.scrollIntoView({ block: "end", behavior: "smooth" });
}


function getMessages() {
    fetch("/messages")
        .then(response => response.json())
        .then(payload => {
            for (const payloadKey in payload) {
                addMessage(payload[payloadKey]);
            }
        })
        .catch(error => console.log(error));
}



const setup = () => {
    usernameForm.addEventListener('submit', connect, true);
    document.getElementById("send-msg-btn").addEventListener("click", sendMessage);
    document.addEventListener("keydown", ev => {
        if (ev.key === "Enter") sendMessage(ev);
    });
}

setup();

