import pyautogui as pg
import socket
import sys
import threading
import time

pg.FAILSAFE = False
mouse = False
isScrollingUp = False
isScrollingDown = False

sizeX, sizeY = pg.size()
sizeX -= 4
sizeY -= 4


def scrollUpFunc():
    # global isScrollingUp
    while True:
        if(scrollUpFunc.isScrollingUp == 0):
            break
        pg.scroll(20)
        time.sleep(0.1)

scrollUpFunc.isScrollingUp = 0
    

def scrollDownFunc():
    # global isScrollingDown
    while True:
        if(scrollDownFunc.isScrollingDown == 0):
            break
        pg.scroll(-20)
        time.sleep(0.1)

scrollDownFunc.isScrollingDown = 0


def moveCursor(val):
    val = val.split()
    posX, posY = pg.position()
    
    if((posX > 4 and posX < sizeX) or (posY > 4 and posY < sizeY)):
        try:
            pg.moveRel(-float(val[1]), -float(val[0]))
        except:
            return
    elif(posX < 4 and posY < 4):
        pg.moveTo(8, 8)
    elif(posX > sizeX and posY < 4):
        pg.moveTo(sizeX-4, 8)
    elif(posX > sizeX and posY > sizeY):
        pg.moveTo(sizeX-4, sizeY-4)
    else:
        pg.moveTo(8, sizeY-4)

def executeKeyboard(command):
    if(command == "shiftDown"):
        pg.keyDown("shift")
    elif(command == "ctrlDown"):
        pg.keyDown("ctrl")
    elif(command == "shiftUp"):
        pg.keyUp("shift")
    elif(command == "ctrlUp"):
        pg.keyUp("ctrl")
    else:
        pg.press(command)

def executeMouse(command):
    if(command == 'L'):
        pg.click()

    elif(command == 'R'):
        pg.click(button='right')

    elif(command == 'M'):
        pg.click(button='middle')

    elif(command == 'U'):
        if(scrollUpFunc.isScrollingUp == 1):
            scrollUpFunc.isScrollingUp = 0

        if(scrollDownFunc.isScrollingDown == 1):
            scrollDownFunc.isScrollingDown = 0

        pg.scroll(20)

    elif(command == 'D'):
        if(scrollUpFunc.isScrollingUp == 1):
            scrollUpFunc.isScrollingUp = 0

        if(scrollDownFunc.isScrollingDown == 1):
            scrollDownFunc.isScrollingDown = 0

        pg.scroll(-20)

    elif(command == 'LongScrollUp'):
        if(scrollDownFunc.isScrollingDown):
            scrollDownFunc.isScrollingDown = 0

        if(scrollUpFunc.isScrollingUp == 0):
            scrollUpFunc.isScrollingUp = 1
            threading.Thread(target=scrollUpFunc).start()
        else:
            scrollUpFunc.isScrollingUp = 0


    elif(command == 'LongScrollDown'):
        if(scrollUpFunc.isScrollingUp):
            scrollUpFunc.isScrollingUp = 0

        if(scrollDownFunc.isScrollingDown == 0):
            scrollDownFunc.isScrollingDown = 1
            threading.Thread(target=scrollDownFunc).start()
        else:
            scrollDownFunc.isScrollingDown = 0

    else:
        moveCursor(command)

def get_ip_address():

    print("Make sure your PC and Phone is connected to same network")
    print("If connected press enter to continue")

    notConnected = True

    while(notConnected):
        input()

        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(("8.8.8.8", 80))
            notConnected = False
        except:
            print("Your PC is not connected to any network")
            print("Please try again !!!")
            print()
            print("If connected press enter to continue")
            notConnected = True
            # pg.sleep(3)
            # sys.exit()

        if(notConnected == False):
            break
        
    return s.getsockname()[0]



HOST = get_ip_address()
PORT = 5000
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("IP: {0}".format(HOST))

try:
    s.bind((HOST, PORT))
except socket.error as err:
    print("Connection Failed!!!")
    sys.exit()

print('Bind Succesfull')

s.listen(10)
print('Now listening....')

while 1:
    conn, addr = s.accept()
    buf = conn.recv(1023).decode('utf-8')

    if(buf == 'keyboard'):
        mouse = False
    elif(buf == 'mouse'):
        mouse = True
    elif (buf == 'exit'):
        break

    if(mouse):
        threading.Thread(target=executeMouse, args=(buf,)).start()
    else:
        threading.Thread(target=executeKeyboard, args=(buf,)).start()

s.close()
sys.exit()