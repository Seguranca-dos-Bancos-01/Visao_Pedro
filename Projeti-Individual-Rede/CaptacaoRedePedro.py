
import speedtest
import socket
import datetime
import time
import psutil
from mysql.connector import connect

# Função para obter a conexão com o banco de dados
def mysql_connection(host, user, passwd, database=None):
    connection = connect(
        host=host,
        user=user,
        passwd=passwd,
        database=database
    )
    return connection

# Conectar ao banco de dados
connection = mysql_connection('localhost', 'root', 'Pedroca12@', 'SecurityBank')

print("\nIniciando Seu Monitoramento...\r\n")

# Função para medir a velocidade da Internet
def get_speed():
    st = speedtest.Speedtest()
    st.get_best_server()
    download_speed = st.download() / 1024 / 1024  # Convertendo para Mbps
    upload_speed = st.upload() / 1024 / 1024  # Convertendo para Mbps
    return download_speed, upload_speed

# Função para medir o ping
def get_ping():
    st = speedtest.Speedtest()
    st.get_best_server()
    ping = st.results.ping
    return ping

# Função para obter o endereço IP da rede local
def get_network_ip():
    try:
        hostname = socket.gethostname()
        network_ip = socket.gethostbyname(hostname)
        return network_ip
    except socket.gaierror:
        return 'N/A'

while True:
    # Obter o IP da rede
    network_ip = get_network_ip()
    print(network_ip)

    # Obter status da rede
    network_connections = psutil.net_connections()
    network_active = any(conn.status == psutil.CONN_ESTABLISHED for conn in network_connections)
    status = 1 if network_active else 0

    # Medir a velocidade da Internet
    download_speed, upload_speed = get_speed()
    download = round(download_speed, 2)
    upload = round(upload_speed, 2)

    # Medir o ping
    ping_value = get_ping()
    ping = round(ping_value, 2)

    # Data e hora atual
    current_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    # Query de inserção
    query = '''
        INSERT INTO Rede(ip, status, PotenciaUpload, PotenciaDownload, Ping, dtHora, fkServidor, fkBanco, fkEspecificacoes, fkPlano)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
    '''

    # Valores para inserção
    insert_values = (network_ip, status, upload, download, ping, current_time, 1, 1, 1, 1)

    # Criar cursor
    cursor = connection.cursor()

    # Executar a query
    cursor.execute(query, insert_values)
    connection.commit()

    # Exibir os resultados
    print(f"Status da Rede: {status}")
    print(f"IP da rede local: {network_ip}")
    print(f"Velocidade de Download: {download:.2f} Mbps")
    print(f"Velocidade de Upload: {upload:.2f} Mbps")
    print(f"Ping: {ping} ms\r\n")

    time.sleep(5)

 