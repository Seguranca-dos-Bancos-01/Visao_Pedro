import java.io.File
import javax.swing.JOptionPane

object codigoPy {

    private const val nomeArquivoControle = "stop.txt"
    private const val nomeArquivoPython = "captacaoIndividual.py"

    fun monitoramento () {

        File(nomeArquivoControle).delete()
        val repositorio = Repositorio()

        repositorio.iniciar()
        repositorio.validarColaborador()

        val fkBanco = repositorio.fkBanco
        val fkPlano = repositorio.fkPlano
        val fkServidor = repositorio.fkServidor
        val fkEspicificacao = repositorio.fkEspicificacao


        val codigo = """
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

    cursor.execute("SELECT LAST_INSERT_ID();")
    last_id_rede = cursor.fetchone()[0]

    if ping > 10:
        alert_query = '''
            INSERT INTO alertaRede(componente, data, hora, status, fkRede)
            VALUES (%s, %s, %s, %s, %s);
        '''
        alert_values = ('Ping', datetime.datetime.now().strftime('%Y-%m-%d'), datetime.datetime.now().strftime('%H:%M:%S'), 'Ping Critico', last_id_rede)
        cursor.execute(alert_query, alert_values)
        connection.commit()
        print("Alerta de Ping inserido na tabela alertaRede!")

    # Verificar download menor que 200
    if download < 200:
        alert_query = '''
            INSERT INTO alertaRede(componente, data, hora, status, fkRede)
            VALUES (%s, %s, %s, %s, %s);
        '''
        alert_values = ('Download', datetime.datetime.now().strftime('%Y-%m-%d'), datetime.datetime.now().strftime('%H:%M:%S'), 'Download Critico', last_id_rede)
        cursor.execute(alert_query, alert_values)
        connection.commit()
        print("Alerta de Download inserido na tabela alertaRede!")

    # Verificar upload menor que 200
    if upload < 200:
        alert_query = '''
            INSERT INTO alertaRede(componente, data, hora, status, fkRede)
            VALUES (%s, %s, %s, %s, %s);
        '''
        alert_values = ('Upload', datetime.datetime.now().strftime('%Y-%m-%d'), datetime.datetime.now().strftime('%H:%M:%S'), 'Upload Critico', last_id_rede)
        cursor.execute(alert_query, alert_values)
        connection.commit()
        print("Alerta de Upload inserido na tabela alertaRede!")

    # Verificar status igual a 0
    if status == 0:
        alert_query = '''
            INSERT INTO alertaRede(componente, data, hora, status, fkRede)
            VALUES (%s, %s, %s, %s, %s);
        '''
        alert_values = ('Status', datetime.datetime.now().strftime('%Y-%m-%d'), datetime.datetime.now().strftime('%H:%M:%S'), 'Rede Desconectada', last_id_rede)
        cursor.execute(alert_query, alert_values)
        connection.commit()
        print("Alerta de Status igual a 0 inserido na tabela alertaRede!")





    
    # Exibir os resultados
    print(f"Status da Rede: {status}")
    print(f"IP da rede local: {network_ip}")
    print(f"Velocidade de Download: {download:.2f} Mbps")
    print(f"Velocidade de Upload: {upload:.2f} Mbps")
    print(f"Ping: {ping} ms\r\n")

    time.sleep(5)

 """
        val nomeArquivo = "CaptacaoRedePedro.py"

        File(nomeArquivo).writeText(codigo)
        Runtime.getRuntime().exec("py $nomeArquivo")
    }
}
