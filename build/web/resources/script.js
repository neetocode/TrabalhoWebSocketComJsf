
if (!verificaToken()) {
    close();
}
const mainContainer = document.getElementById("main-container");
const btnEnviar = document.getElementById("btnEnviar");
const txtMsg = document.getElementById("txtMsg");
const usersContainer = document.getElementById("users");
var usuarios = [],
        userCurrent = {username: null, id: null},
        userToken,
        idUserFromCurrent = null;
const ws = new WebSocket("ws://localhost:8080/TrabalhoWebSocket/chat");

ws.onopen = event => {
    console.log("conectado");
    autentica();
};

ws.onmessage = event => {
    var data = JSON.parse(event.data);
    console.log(data);
    switch (data.type) {
        case "message":
            var chat = getChat(data.from);
            chat.insertAdjacentHTML('beforeend', renderUserMessage(data.userFrom, data.message, data.sameOrigin));
            chat.scrollTop = chat.scrollHeight;
            break;
        case "system":
            console.log(data.message);
            break;
        case "authentication":
            userCurrent.username = data.username;
            userCurrent.id = data.id;
            console.log(data.message);
            break;
        case "users":
            var usuarios = JSON.parse(data.data);
            console.log(usuarios);
            usersContainer.innerHTML = renderUsuarios(usuarios);
            createChats(usuarios);
            break;
    }
};

ws.onclose = event => {
    debugger
    console.log("desconectado");
};

btnEnviar.addEventListener('click', sendChatMessage);
function showChat(idFrom) {
    debugger
    chat = getChat(idFrom);
    if (chat !== null) {
        var allChats = document.querySelectorAll('.chat-container');
        allChats.forEach(item => {
            item.style.display = 'none';
        });
        chat.style.display = 'block';
        idUserFromCurrent = idFrom;
    }
}
function getChat(idFrom) {
    var chat = document.querySelector('.chat-container[data-from="' + idFrom + '"');
    return chat;

}
function createChats(usuarios) {
    usuarios.forEach(usuario => {
        if (usuario.id === userCurrent.id)
            return;
        var chat = getChat(usuario.id);
        if (chat === null){
            mainContainer.insertAdjacentHTML('beforeend', renderChat(usuario));
        } 
    });
}

function renderChat(userFrom) {
    var retorno = "";
    retorno = '<div class="chat-container" style="display: none" data-from="' + userFrom.id + '">' +
            '<div class="chat-title"><span class="username">' + userFrom.username + '</span></div>' +
            '<div class="chat"></div>' +
            '</div>';
    return retorno;
}

function renderUsuarios(usuarios) {
    var retorno = usuarios.map(usuario => {
        var retorno;
        if (usuario.id === userCurrent.id) {
            retorno = '<div class="user actual">';
        } else {
            retorno = '<div class="user" data-id="' + usuario.id + '" onclick="showChat(\''+usuario.id+'\')">';
        }

        retorno += '<span>' + usuario.username + '</span></div>';
        return retorno;
    });
    return retorno.join('');
}
function userClick(event){
    debugger
}
function close() {
    window.history.replaceState({}, 'login', '/TrabalhoWebSocket/faces/login.xhtml');
    window.history.go('/TrabalhoWebSocket/faces/login.xhtml');
}
function sendChatMessage(e) {
    e.preventDefault();
    if (txtMsg.value.trim().length === 0)
        return;
    const payload = {
        message: txtMsg.value,
        to: idUserFromCurrent,
        type: 'message'
    };
    txtMsg.value = '';
    txtMsg.focus();
    ws.send(JSON.stringify(payload));
}
function autentica() {
    const payload = {
        type: 'authentication',
        token: userToken
    };
    ws.send(JSON.stringify(payload));
}
function renderUserMessage(username, message, sameOrigin) {
    return '<div class="user-message' + (sameOrigin ? ' sameOrigin' : '') + '">' +
            '<span class="username">' + username + '</span>' +
            '<span class="message">' + message + '</span>' +
            '</div>';
}
function renderSystemMessage(message) {
    return '<div class="system-message">' +
            '<span class="message">' + message + '</span>' +
            '</div>';
}

function verificaToken() {
    var token = getCookie("token");
    if (token === null || token === undefined)
        return false;
    if (token.toString().length === 0)
        return false;
    userToken = token;
    return true;
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i in ca) {
        var c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return null;
}

/*
 * mensagem
 * idUsuarioDestino
 * token
 */
txtMsg.focus();
