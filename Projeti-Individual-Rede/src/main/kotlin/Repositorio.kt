import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCallback
import java.util.*
import javax.swing.JOptionPane
import javax.swing.JOptionPane.*

class Repositorio {

    var fkBanco:Int = 0
    var fkPlano:Int = 0
    var fkServidor:Int = 0
    var fkEspicificacao:Int = 0
    var fkMetrica:Int =0
    var fkLocacao:Int =0
    lateinit var jdbcTemplate: JdbcTemplate

    fun iniciar() {
        jdbcTemplate = Conexao.jdbcTemplate!!

    }

    fun validarColaborador() {

        val email = showInputDialog("Digite Seu Email:")
        val senha = showInputDialog("Digite Sua Senha:")

        // puxando o colaborador
        val loginQuery = "SELECT COUNT(*) FROM funcionarios WHERE email = ? AND senha = ?"
        val count = jdbcTemplate.execute(loginQuery, PreparedStatementCallback { preparedStatement ->
            preparedStatement.setString(1, email)
            preparedStatement.setString(2, senha)
            val resultSet = preparedStatement.executeQuery()
            resultSet.next()
            resultSet.getInt(1)
        })

        // validando se existe no banco
        if (count != null) {
            if (count > 0) {


                val nomeQuery = "SELECT nome FROM funcionarios WHERE email = ? AND senha = ?"
                val nome = jdbcTemplate.execute(nomeQuery, PreparedStatementCallback { preparedStatement ->
                    preparedStatement.setString(1, email)
                    preparedStatement.setString(2, senha)
                    val nomeResultSet = preparedStatement.executeQuery()
                    nomeResultSet.next()
                    nomeResultSet.getString("nome")
                })

                // puxando a fkEscalonamento
                val fkEscalonamentoQuery = "SELECT fkEscalonamento FROM funcionarios WHERE email = ? AND senha = ?"
                val fkEscalonamento = jdbcTemplate.execute(fkEscalonamentoQuery, PreparedStatementCallback { preparedStatement ->
                    preparedStatement.setString(1, email)
                    preparedStatement.setString(2, senha)
                    val EscalonamentoResultSet = preparedStatement.executeQuery()
                    EscalonamentoResultSet.next()
                    EscalonamentoResultSet.getInt("fkEscalonamento")
                })

                // arrumando o nivel de acesso
                val cargo = when (fkEscalonamento) {
                    1 -> "Admin"
                    2 -> "Operator"
                    3 -> "Estagiario"
                    else -> "Cargo desconhecido"
                }

                showMessageDialog(null, "Bem Vindo(a) $nome - tipo: $cargo")

                // puxando a fkBanco
                val fkBancoQuery = "SELECT fkBanco FROM funcionarios WHERE email = ? AND senha = ?"
                fkBanco = jdbcTemplate.execute(fkBancoQuery, PreparedStatementCallback { preparedStatement ->
                    preparedStatement.setString(1, email)
                    preparedStatement.setString(2, senha)
                    val resultSet = preparedStatement.executeQuery()
                    resultSet.next()
                    resultSet.getInt("fkBanco")
                })

                // puxando a fkPlano
                val fkPlanoQuery = "SELECT s.fkPlano\n" +
                        "FROM funcionarios f\n" +
                        "JOIN servidor s ON f.fkBanco = s.fkBanco\n" +
                        "WHERE f.email = ? AND f.senha = ?;\n"
                fkPlano = jdbcTemplate.execute(fkPlanoQuery, PreparedStatementCallback { preparedStatement ->
                    preparedStatement.setString(1, email)
                    preparedStatement.setString(2, senha)
                    val resultSet = preparedStatement.executeQuery()
                    resultSet.next()
                    resultSet.getInt("fkPlano")
                })

                // mostrando os Servidores disponiveis
                val fkServidorQuery = "SELECT s.idServidor, s.sistemaOperacional, s.enderecoIP, s.cpfResponsavelLegal, s.apelido, b.nomeFantasia\n" +
                        "FROM servidor s\n" +
                        "JOIN banco b ON s.fkBanco = b.idBanco WHERE fkBanco = ${fkBanco};"

                val ServidorDispo = jdbcTemplate.query(fkServidorQuery) { resultSet, _ ->
                    val idServidor = resultSet.getInt("idServidor")
                    val so = resultSet.getString("sistemaOperacional")
                    val ip = resultSet.getString("enderecoIP")
                    val responsavelLegal = resultSet.getString("cpfResponsavelLegal")
                    val apelido = resultSet.getString("apelido")


                    println("ID: $idServidor, SO: $so, IP: $ip, Sala: $responsavelLegal, Apelido da maquina: $apelido")
                }

                println("Número de Servidores disponíveis: ${ServidorDispo.size}")

                // puxando a fkServidor
                val ServidorOpcao = JOptionPane.showInputDialog("Escolha um ID:").toInt()
                fkServidor = ServidorOpcao

                showMessageDialog(
                    null, """
                    Seu monitoramento está rodando
                           Verifique seu Banco!!!
                    """.trimIndent()
                )

                // puxando o fkEspecificacoesRede
                val fkEspecificacoesQuery = "select fkEspecificacoes from servidor WHERE idServidor = ?"
                fkEspicificacao = jdbcTemplate.execute(fkEspecificacoesQuery, PreparedStatementCallback { preparedStatement ->
                    preparedStatement.setInt(1, ServidorOpcao)
                    val resultSet = preparedStatement.executeQuery()
                    resultSet.next()
                    resultSet.getInt("fkEspecificacoes")
                })




                }

            }
    }
}
