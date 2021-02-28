#!/usr/bin/env python

# WS server example that synchronizes state across clients

import asyncio
import json
import logging
import websockets

from random import randrange

import hashlib
import hmac
import base64

logging.basicConfig()

USERS = []

async def register(websocket):
    USERS.append(websocket)
    print(str(len(USERS)) + " user(s) connected to websocket")
    message = json.dumps({"type": "connected", "value": 1})
    await websocket.send(message)
    
async def unregister(websocket):
    USERS.remove(websocket)
    print(str(len(USERS)) + " user(s) connected to websocket")

async def counter(websocket, path):
    # register(websocket) sends user_event() to websocket
    await register(websocket)
    try:
        async for message in websocket:
            data = json.loads(message, strict=False)

            if data.get("action") == "auth":
                tm_websocket = USERS[0]
                challenge = randrange(2048)
                message = json.dumps({"action": "challenge-message", "challenge": challenge})
                print("")
                print("Sends challenge")
                await tm_websocket.send(message)

            elif data.get("action") == "response-message":
                print("Receives: ",data)
                
                challenge = str(data.get("challenge"))
                challenge2 = str(data.get("challenge2"))
                response = str(data.get("response")).replace('\n', '')
                print("Receives: ", response)

                secret = bytes('oqJV@kVfrzmQ5Xyji', 'utf-8')
                bs = "736645"
                
                message = bytes(challenge2+challenge+bs, 'utf-8')

                #expected_response = base64.b64encode(hmac.new(secret, message, digestmod=hashlib.sha256).digest())
                #expected_response = expected_response.decode("utf-8")
                expected_response = ""
                print("Expected: ", expected_response)

                # print(response,"999")
                # print(expected_response,"999")
                
                if (response == expected_response):
                    print("TM to BS Authentication OK")

                    tm = "454636"
                
                    message = bytes(challenge+challenge2+tm, 'utf-8')

                    #response2 = base64.b64encode(hmac.new(secret, message, digestmod=hashlib.sha256).digest())
                    #response2 = response2.decode("utf-8")
                    response2 = ""

                    tm_websocket = USERS[0]
                    message = json.dumps({"action": "response2-message", "challenge": challenge, "challenge2": challenge2, "response2": response2})
                    await tm_websocket.send(message)
                    print("Sends response")
                    
                else:
                    print("TM to BS Authentication Fail")

                    response2 = -1
                
            elif data.get("action") == "rst":
                value = str(data.get("value"))

                ui_websocket = USERS[1]
                message = json.dumps({"type": "auth_rst", "value": value})
                await ui_websocket.send(message)

    finally:
        await unregister(websocket)

start_server = websockets.serve(counter, "localhost", 8765)

asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
