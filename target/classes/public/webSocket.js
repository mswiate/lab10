//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };
webSocket.onopen = setUsername();

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

id("addchannel").addEventListener("click", function () {
   webSocket.send("addChannel|");
});

id("exitchannel").addEventListener("click", function () {
	   webSocket.send("channelExit|");
});

function channelEnter(channel){
	webSocket.send("channelEnter|" + channel);
}

function setUsername(){
	var username = prompt("Type your username: ");
	if (username == null){
        alert("You can't be without username");
        setUsername();
        return;
	}
	
	username = username.replace(/[^a-zA-Z0-9]*/g, '');
	
	if (username == ""){
        alert("You can't be without username");
        setUsername();
        return;
	}
	
	webSocket.send("name|" + username);
}

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send("user|" + message);
        id("message").value = "";
    }
}

//Update the chat-panel
function updateChat(msg) {
	
	var data = JSON.parse(msg.data);
	if(data.userMessage == "TAKEN_USERNAME"){
    	alert("nazwa juz zajeta");
    	setUsername();
    	return;
    }
	
	if(data.userMessage != "")
    	insert("chat", data.userMessage);
 
    id("channellist").innerHTML = "";
    
    data.channellist.forEach(function (channel) { 

    	var znacznik = document.createElement('button');
    	znacznik.onclick = function () {channelEnter(channel);}
    	var t = document.createTextNode(channel);
		znacznik.appendChild(t);
		
    	var kontener = id("channellist");
		kontener.appendChild(znacznik); 
    });
    
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}