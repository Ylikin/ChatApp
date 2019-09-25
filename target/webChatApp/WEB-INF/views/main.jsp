<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
    <title>Title</title>
    <style>
        .chatbox {
            /*display:none;*/
            /*delete*/
        }

        .messages {
            background-color: dodgerblue;
            width: 500px;
            padding: 20px;
            overflow-x: hidden;
            max-height: 400px;
            min-height: 400px;

        }

        .messages .msg {

            background-color: #fff;
            border-radius: 10px;
            margin-bottom: 10px;


        }

        .messages .msg {
            word-wrap: break-word;
            background-color: darkblue;
            line-height: 30px;
            text-align: center;
            color: white;
        }

        .messages .msg .from {
            background-color: deepskyblue;
            line-height: 30px;
        }

        .messages .msg .text {
            padding: 10px;

        }

        .bord {

            max-width: 540px;
            border: 2px outset black;
        }

        textarea.msg {
            width: 540px;
            padding: 10px;
            resize: none;
            border: none;
            box-shadow: 2px 2px 5px 0 inset;
        }

        .SendButton {
            background-color: deepskyblue;
            border-radius: 5px;
            min-width: 100px;
            min-height: 40px;
            border-color: deepskyblue;
            color: white;
        }

        .alert {
            max-width: 530px;
            max-height: 40px;
            padding: 15px;
            border: 1px solid #d6e9c6;
            border-radius: 4px;
            color: white;
            background-color: red;
        }
    </style>
    <script>
        let ws;
        let reg;
        let check = false;
        let k;
        let chatUnit = {
            init() {

                this.chatbox = document.querySelector(".chatbox");
                this.startSendBtn = this.chatbox.querySelector(".SendButton");


                this.msgTextArea = this.chatbox.querySelector("textarea");
                this.chatMessageContainer = this.chatbox.querySelector(".messages");

                this.bindEvents();
            },
            bindEvents() {

                // this.startBtn.addEventListener("click", e => this.openSocket());

                this.startSendBtn.addEventListener("click", e => {
                    if (this.msgTextArea.value) {
                        this.send();
                    }
                });

                // this.msgTextArea.addEventListener("keyup",e=>{
                //     if(e.keyCode===13)
                //     {
                //         e.preventDefault();
                //         this.send(this.msgTextArea.value);
                //     }
                // })
            },
            send() {
                if (!check) {
                    this.register();
                } else {

                    this.sendMessage({
                        name: this.name = this.nameInput,
                        role: this.role = this.roleInput,
                        text: this.msgTextArea.value
                    });


                }
            },

            onOpenSock() {
                this.send();
            },
            onMessage(msg) {

                let msgBlock = document.createElement("div");
                msgBlock.className = "msg";
                let fromBlock = document.createElement("div");
                fromBlock.className = "from";
                fromBlock.innerText = msg.name;
                let textBlock = document.createElement("div");
                textBlock.className = "text";
                textBlock.innerText = msg.text;

                msgBlock.appendChild(fromBlock);
                msgBlock.appendChild(textBlock);
                this.chatMessageContainer.appendChild(msgBlock);

            },
            onClose() {

            },
            sendMessage(msg) {
                if (k) {
                    k = false;
                    this.ws.send(JSON.stringify(msg));
                } else if (k === false) {
                    this.onMessage({name: "Me", text: msg.text});
                    this.msgTextArea.value = "";
                    this.ws.send(JSON.stringify(msg));
                }
            },
            openSocket() {
                this.ws = new WebSocket("ws://localhost:8080/webChatApp_war/chat");
                this.ws.onopen = () => this.onOpenSock();
                this.ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
                this.ws.onclose = (e) => this.onClose();
                this.name = this.nameInput.value;
                this.role = this.roleInput.value;
                this.chatbox.style.display = "block";


            },
            register() {

                reg = this.msgTextArea.value;
                this.msgTextArea.value = "";
                if (reg.startsWith("/register agent ") || reg.startsWith("/register client ")) {
                    let register1 = reg.split(" ");
                    if (register1.length === 3) {
                        this.nameInput = register1[2];
                        this.roleInput = register1[1];
                        check = true;
                        k = true;
                        this.openSocket();
                    } else {

                    }
                } else {
                    let div = document.createElement("div");
                    div.className = "alert";
                    div.innerHTML = "<strong>Incorrect specified parameters.</strong> /register (agent/client) (nickname)";

                    this.chatbox.appendChild(div);
                    setTimeout(() => {
                        this.chatbox.removeChild(div)
                    }, 5000);

                }


            }


        };

        window.addEventListener("load", e => chatUnit.init());

    </script>
</head>
<body>
<h1>Chat</h1>
<div class="chatbox">
    <section class="bord">
        <div class="messages">

        </div>
        <label>
            <textarea class="msg"></textarea>
        </label>
    </section>
    <p>
        <button class="SendButton" id="chatbox">submit</button>
    </p>
</div>
</body>
</html>
