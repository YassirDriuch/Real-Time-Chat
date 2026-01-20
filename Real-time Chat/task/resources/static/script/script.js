let stompClient = null;

const loginPage = document.getElementById('loginPanel');
const messagePage = document.getElementById('container');
const usernameInput = document.getElementById('input-username');
const usernameForm = document.getElementById('userForm');

function connect(event) {
    let socket = new SockJS('http://'+ document.location.host +'/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
    event.preventDefault();
}

function onConnected() {
    let usernameObj = {
        username: usernameInput.value.trim()
    };
    stompClient.send(
        '/app/chat.registerUser',
        {},
        JSON.stringify(usernameObj)
    );
    loginPage.classList.toggle('hidden');
    messagePage.classList.toggle('hidden');
    stompClient.subscribe('/topic/public', onMessageReceived);
    getMessages();
}
function onMessageReceived(payload) {
    console.log(payload);
    addMessage(JSON.parse(payload.body));
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
    if (messageObj.content.length > 0){
        const newNode = document.createElement("div");
        newNode.classList.add("message-container");
        const messageDataNode = document.createElement("div");
        messageDataNode.classList.add("message-data");
        let spanUser = document.createElement("span");
        spanUser.classList.add("sender");
        spanUser.textContent = messageObj.user;
        let spanTime = document.createElement("span");
        spanTime.classList.add("date");
        spanTime.textContent = messageObj.timestamp;
        messageDataNode.append(spanUser, spanTime)
        const spanMessageValue = document.createElement("span");
        spanMessageValue.classList.add("message");
        spanMessageValue.textContent = messageObj.content;
        newNode.append(messageDataNode, spanMessageValue);
        let messageContainer = document.getElementById("messages");
        if (document.querySelector(".message") != null) {
            messageContainer.insertAdjacentHTML("beforeend", "<hr>");
        }
        messageContainer.appendChild(newNode);
        newNode.scrollIntoView({ block: 'end',  behavior: 'smooth' });
    }
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

