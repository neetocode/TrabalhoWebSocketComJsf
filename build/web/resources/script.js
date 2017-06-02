var logout, showChat;
(function () {
    if (!verificaToken()) {
        close();
        return;
    }
    const mainContainer = document.getElementById("main-container");
    const btnEnviar = document.querySelector("#actions-container .btnEnviar");
    const form = document.querySelector("#actions-container form");
    const txtMsg = document.querySelector("#actions-container .txtMsg");
    const usersContainer = document.getElementById("users");
    const actionsContainer = document.getElementById("actions-container");
    var usuarios = [],
            userCurrent = {username: null, id: null},
            userToken,
            idUserFromCurrent = null;

    const ws = new WebSocket("ws://localhost:8080/TrabalhoWebSocket/chat?t=" + userToken);

    ws.onopen = event => {
        console.log("conectado");
    };

    ws.onmessage = event => {
        var data;
        try {
            data = JSON.parse(event.data);
        } catch (ex) {
            data = event.data;
        }

        console.log(data);
        switch (data.type) {
            case "message":
                //data.userFrom = JSON.parse(data.userFrom);
                var chatContainer = getChat(data.userFrom.id);
                var chat = chatContainer.querySelector(".chat");
                chat.insertAdjacentHTML('beforeend', renderUserMessage(data.userFrom, data.message, data.sameOrigin));
                chat.scrollTop = chat.scrollHeight;
                if (idUserFromCurrent !== data.userFrom.id) {
                    notificaMensagem(data.userFrom.id);
                }
                break;
            case "system":
                var chatContainer = getChat(idUserFromCurrent);
                var chat = chatContainer.querySelector(".chat");
                chat.insertAdjacentHTML('beforeend', renderSystemMessage(data.message));
                chat.scrollTop = chat.scrollHeight;
                break;
            case "authentication":
                userCurrent.username = data.username;
                userCurrent.id = data.id;
                document.querySelector("#profile .profile-name").innerHTML = userCurrent.username;
                document.querySelector("#profile .profile-id").innerHTML = '#' + userCurrent.id;
                console.log(data.message);
                break;
            case "users":
                var usuarios = JSON.parse(data.data);
                console.log(usuarios);
                var conteudo = renderUsuarios(usuarios);
                usersContainer.innerHTML = conteudo;
                createChats(usuarios);
                break;
        }
    };

    ws.onclose = event => {
        console.log("desconectado");
        close();
    };

    form.addEventListener('submit', function(e){
        e.preventDefault();
        sendChatMessage();
    });
    function notificaMensagem(userFromId) {
        var userElm = document.querySelector(".user[data-id='" + userFromId + "']");
        userElm.classList.add("new-message");
    }
    function removeNotificaMensagem(userFromId) {
        var userElm = document.querySelector(".user[data-id='" + userFromId + "']");
        userElm.classList.remove("new-message");
    }
    showChat = function showChat(userFromId) {
        chat = getChat(userFromId);
        if (chat !== null) {
            var allChats = document.querySelectorAll('.chat-container');
            [].forEach.call(allChats, item => {
                item.style.display = 'none';
            });
            chat.style.display = 'block';
            idUserFromCurrent = userFromId;
            removeNotificaMensagem(userFromId);
            actionsContainer.style.display = 'flex';
            txtMsg.focus();
        }
    };
    function getChat(idFrom) {
        var chat = document.querySelector('.chat-container[data-from="' + idFrom + '"');
        return chat;

    }
    function createChats(usuarios) {
        usuarios.forEach(usuario => {
            if (usuario.id === userCurrent.id)
                return;
            var chat = getChat(usuario.id);
            if (chat === null) {
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
            var retorno = "";
            if(!userCurrent.id) return "";
            if (usuario.id !== userCurrent.id) {
                if (usuario.online) {
                    retorno += '<div class="user online" data-id="' + usuario.id + '" onclick="showChat(\'' + usuario.id + '\')">';
                } else {
                    retorno += '<div class="user" data-id="' + usuario.id + '">';
                }

                retorno += '<span>' + usuario.username + '</span><i class="fa fa-circle"></i></div>';
            }
            return retorno;
        });
        return retorno.join('');
    }
    function close() {
        window.history.replaceState({}, 'login', '/TrabalhoWebSocket/faces/login.xhtml');
        window.history.go('/TrabalhoWebSocket/faces/login.xhtml');
    }
    logout = function logout() {
        deleteAllCookies();
        close();
    };
    function sendChatMessage() {
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
    /*
     function autentica() {
     const payload = {
     type: 'authentication',
     token: userToken
     };
     ws.send(JSON.stringify(payload));
     }*/
    function renderUserMessage(userFrom, message, sameOrigin) {
        var date = new Date();
        return '<div class="user-message' + (sameOrigin ? ' sameOrigin' : '') + '">' +
                '<span class="username">' + userFrom.username + '</span>' +
                '<span class="message">' + message + '</span>' +
                '<span class="time">' + date.toLocaleTimeString() + '</span>' +
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
    function deleteAllCookies() {
        var cookies = document.cookie.split(";");

        for (var i = 0; i < cookies.length; i++) {
            var cookie = cookies[i];
            var eqPos = cookie.indexOf("=");
            var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
            document.cookie = name + "=;expires=2000-01-01T00:00:00.000Z";
        }
    }
    /*
     * mensagem
     * idUsuarioDestino
     * token
     */
})();
