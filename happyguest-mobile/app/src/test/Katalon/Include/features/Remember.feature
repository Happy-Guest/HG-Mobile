@tag
Feature: Lembrar sessão
  Como: Cliente
  Quero: Entrar diretamente na aplicação mobile
  Para: Não ser necessário voltar a inserir os meus dados de acesso no login

  @tag1
  Scenario: AT1- Utilizador não lembrou a sessão
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "XPTO1" no campo de palavra-passe
    And clica no botão "Login" no ecrã login
    Then é apresentado a página "Home" sem sessão guardada

  @tag2
  Scenario: AT2- Utilizador lembrou a sessão
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "XPTO1" no campo de palavra-passe
    And clica no botão “Lembrar“ na secção de Login
    And clica no botão "Login" no ecrã login
    Then é apresentado a página "Home" com sessão guardada

  @tag3
  Scenario: AT3- Utilizador entrou na app com Sessão iniciada
    Given a aplicação está pronta
    When é apresentada a página "Home"
    Then escreve no ecrã "Sessão restaurada com sucesso." - 16
