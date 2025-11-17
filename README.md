SISTEMA DE GERENCIAMENTO DE PROJETOS

- Criar projetos com data de inicio e fim, com descrição e nome;
- Criar tarefas com nome e descrição que possam ser anexadas a projetos e que seja possível marcar as tarefas como concluídas;
- Seja possível visualizar todos os projetos e tarefas e também edita-los;
- O usuário deve receber uma notificação quando a data de conclusão do projeto estiver próxima.

Plano de Teste - Sistema de Gerenciamento de Projetos

1. Introdução
Este plano de teste descreve a abordagem para testar o Sistema de
Gerenciamento de Projetos, que tem como objetivo permitir que os usuários
criem, acompanhem e gerenciem seus Projetos e tarefas em desenvolvimento de forma eficiente. O
objetivo deste plano é garantir a qualidade e confiabilidade do software antes do
lançamento.

2. Objetivos
Os objetivos do teste são:
- Verificar se todas as funcionalidades do sistema de gerenciamento de projetos
estão implementadas corretamente.
- Validar se o sistema atende aos requisitos funcionais.
- Identificar e corrigir defeitos encontrados durante o teste.

3. Escopo
O teste abrangerá todas as funcionalidades principais do Sistema de
Gerenciamento de Projetos, incluindo:
- Criação, edição e visualização de Projetos e Tarefas.
- Anexação de Tarefas a Projetos.
- Notificações e lembretes sobre os Projetos que estão perto de suas datas de finalização.

4. Estratégia de Teste
A estratégia de teste incluirá:
- Testes unitários realizados pelos desenvolvedores para verificar as
funcionalidades individualmente.
- Testes de integração para garantir que as diferentes partes do sistema
funcionem em conjunto.
- Testes de sistema para verificar o funcionamento do sistema como um todo.
- Testes de aceitação realizados pelos usuários finais para validar o sistema em
um ambiente de produção simulado.

5. Casos de Teste
Serão criados casos de teste para cada funcionalidade do sistema, abrangendo
cenários positivos e negativos, bem como casos de teste de limite e estresse.
Exemplo de Caso de Teste:

Caso de Teste 1 - Criar Projeto
- Descrição: Verificar se é possível criar um novo projeto no sistema.
- Pré-condições: O usuário está logado no sistema.
- Passos:
 1. Acessar a página de criação de projetos.
 2. Preencher o formulário com os dados do novo projeto (nome, descrição, data de início,
data de fim).
 3. Clicar no botão "Salvar".
- Resultado Esperado: A tarefa é criada e exibida na lista de tarefas do usuário.
6. Ambiente de Teste
O teste será realizado em um ambiente de teste dedicado, replicando o ambiente
de produção o mais próximo possível.

7. Recursos
- Equipe de Teste: 2 testadores.
- Ambiente de Teste: Servidor dedicado com configuração similar ao ambiente
de produção.
- Dados de Teste: Conjunto de dados de teste representativos serão criados para
simular cenários reais.

8. Cronograma de Atividades

A execução dos testes está planejada para um período de duas semanas, com a seguinte distribuição de tarefas:

Primeira Semana: Foco nos testes unitários e de integração, garantindo que os componentes individuais e a comunicação entre eles funcionem corretamente.
Segunda Semana: Dedicada aos testes de sistema e de aceitação, validando o sistema como um todo e confirmando que atende aos requisitos do usuário.

9. Condições para Aprovação

O sistema será considerado aprovado e pronto para a próxima fase quando os seguintes critérios forem atendidos:

A totalidade dos cenários de teste planejados deve ser executada com êxito.
Todas as falhas consideradas críticas ou de alto impacto devem ser solucionadas e sua correção devidamente validada pela equipe de testes.

10. Possíveis Riscos

Identificamos os seguintes riscos que podem impactar o plano de testes:

Eventuais atrasos no ciclo de desenvolvimento podem comprometer o cumprimento dos prazos estabelecidos para os testes.
Existe a possibilidade de surgirem problemas de incompatibilidade da aplicação com diferentes navegadores de internet ou modelos de dispositivos.

11. Papéis e Responsabilidades

As atribuições das equipes estão definidas da seguinte forma:

Equipe de Desenvolvimento: Terá a responsabilidade de analisar e corrigir os defeitos que forem identificados e reportados durante a fase de testes.
Equipe de Testes: Ficará encarregada de realizar a execução dos casos de teste, documentar os resultados e comunicar as falhas encontradas.

12. Fluxo de Comunicação

Para manter todas as partes interessadas informadas, serão elaborados e distribuídos relatórios periódicos sobre o andamento dos testes. Esses documentos serão compartilhados com os times de desenvolvimento e com a gerência, servindo como base para o acompanhamento do progresso e para a tomada de decisões estratégicas.

13. Validação do Plano

Antes que qualquer atividade de teste se inicie, este documento será submetido à análise e aprovação da liderança de desenvolvimento. Modificações futuras neste plano só serão implementadas após serem comunicadas e consentidas por todos os envolvidos e interessados no projeto.

14. Considerações Finais
Este plano de teste tem a função de que o Sistema de
Gerenciamento de Tarefas cumpra todos os requisitos.
