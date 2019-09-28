let reg;
let check = false;
let checkRegister;
let chatUnit = {
    init() {

        this.chatbox = document.querySelector(".chatBox");
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
        }
        else {
            if(this.msgTextArea.value==="/exit"){
                this.chatbox.style.display = "none";
            }
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
        if (checkRegister) {
            checkRegister = false;
            this.ws.send(JSON.stringify(msg));
        } else if (checkRegister === false) {
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
                checkRegister = true;
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