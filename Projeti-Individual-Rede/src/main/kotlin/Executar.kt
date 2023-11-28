package app

import Repositorio

fun main() {
    // 1. Conexão com o Banco de Dados
    Conexao.jdbcTemplate // Inicializa a conexão
    // 2. Criar uma instância de Repositorio e chamar seus métodos
    val repositorio = Repositorio()
    repositorio.iniciar()
    // 3. Executar a função monitoramento() de codigoPy
    codigoPy.monitoramento()



    println("Execução concluída.")
}
