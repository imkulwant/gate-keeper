from flask import Flask, jsonify
from datetime import datetime

app = Flask(__name__)

@app.route('/api/ping', methods=['GET'])
def get_current_datetime():
    # Get the current date and time
    current_datetime = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    # Create a response dictionary
    response = {
        'current_datetime': current_datetime,
        'status_code': 200,
        'server': 'Server 2'
    }

    # Return the response as JSON
    return jsonify(response), 200

if __name__ == '__main__':
    # Run the application on localhost:8082
    app.run(host='127.0.0.1', port=8082, debug=True)
