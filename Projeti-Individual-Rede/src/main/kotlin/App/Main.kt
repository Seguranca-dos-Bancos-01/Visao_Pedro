package app

import Repositorio

open class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {

            // 1. Conexão com o Banco de Dados
            Conexao.jdbcTemplate // Inicializa a conexão
            // 2. Criar uma instância de Repositorio e chamar seus métodos
            val repositorio = Repositorio()
            repositorio.iniciar()
            // 3. Executar a função monitoramento() de codigoPy
            codigoPy.monitoramento()





        }
    }
}
