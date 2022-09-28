// import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
// import http from 'k6/http';
import ws from 'k6/ws';
import {check, sleep} from "k6";

const isNumeric = (value) => /^\d+$/.test(value);

const iterations_param = `${__ENV.ITERATIONS}`;
const iterations = isNumeric(iterations_param) ? Number(iterations_param) : 500;

export const options = {
    vus: 1,
    iterations: 1
};

export default function () {
    const url = 'ws://petrace.mycodefu.com:8080/pet-race';
    const params = { };
    let messagesReceived = -2; //ignore the first two because there are two messages sent on open
    let messagesSent = 0;
    const res = ws.connect(url, params, function (socket) {
        socket.on('open', () => {
            console.log('connected');
            socket.setInterval(function timeout() {
                if (messagesReceived >= messagesSent && messagesSent < iterations) {
                    let team = messagesSent % 3 ? 'dog' : 'cat';
                    socket.send(`vote:${team}`);
                    messagesSent++;
                    console.log(`Voted for ${team}. Messages sent: ${messagesSent}, Messages received: ${messagesReceived}`);
                }
            }, 1);
        });
        socket.on('message', (data) => {
            messagesReceived++;
            console.log(`Received message: ${data}. Messages sent: ${messagesSent}, Messages received: ${messagesReceived}`);
            if (messagesReceived >= messagesSent && messagesSent >= iterations) {
                socket.close();
            }
        });
        socket.on('close', () => console.log('disconnected'));
    });

    check(res, { 'status is 101': (r) => r && r.status === 101 });

    sleep(0.100);
}
