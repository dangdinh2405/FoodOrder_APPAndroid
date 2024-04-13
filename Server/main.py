from flask import Flask, jsonify, request, render_template
from flask_sock import Sock

app = Flask(__name__)
sock = Sock(app)
wses =  []

# 2 trang thai: preparing (mới đặt món, đang làm), completing (sắp xong). Đơn đã xong không lưu vào 
menu = {
    "0": "Cơm chiên Dương Châu",
    "1": "Cơm gà xối mỡ",
    "2": "Phở bò tẩm bổ",
    "3": "Mì xào giòn"
}

orders = [
    # {'orderId': '1', 'status': 'preparing', 'foods': [menu["0"]]},
    # {'orderId': '2', 'status': 'completing', 'foods': [menu["1"], menu["2"]]}
]

@app.route("/", methods = ["GET"])
def home():
    return render_template('index.html')

@app.route("/orders", methods = ["GET"])
def getOrdersList():
    return render_template('orders.html', orders=orders)

@app.route("/orderFood", methods = ["POST"])
def orderFood():
    res = request.json
    food = res.get('food')
    foodArr = []
    for i in range(len(food)):
        foodArr.append(menu[food[i]])
    if len(orders) == 0:
        newId = '1'
    else:
        newId = str(int(orders[-1]['orderId']) + 1)
    orders.append(
        {'orderId': newId, 'status': 'preparing', 'foods': foodArr}
    )
    return jsonify({"message": "Data received successfully!"})

@app.route("/changeStatus", methods = ["POST"])
def changeStatus():
    res = request.json
    orderId = res.get('orderId')
    status = res.get('status')
    for i in range(len(orders)):
        if orders[i]['orderId'] == orderId:
            if status == "completed":
                orders.pop(i)
            else:
                orders[i]['status'] = status
    print(orders)
    return jsonify({"message": "Change status successfully!"})


@app.route('/broadcast', methods=['POST'])
def broadcast():
    msg = request.json['msg']
    for ws in wses:
        try:
            ws.send(msg)
        except:
            continue
    return jsonify({
        'success': True
    })

@sock.route('/getFoodStatusUpdate')
def getStatus(ws):
    wses.append(ws)
    while True:
        data = ws.receive(30)
        if data: #data khong null
            ws.send(data)
        else:
            break                                                               
    wses.remove(ws)

if __name__ == '__main__':
    app.run(host = '192.168.1.6', port = 8080)