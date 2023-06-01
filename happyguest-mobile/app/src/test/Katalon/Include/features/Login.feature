@tag
Feature: Login
  	Como: Cliente
  	Quero: Fazer login na aplicação mobile
  	Para: Conseguir aceder à minha conta registada

  @tag1
  Scenario: AT1- Utilizador não tem internet
    Given a aplicação está pronta sem internet
    When clica no botão "Login" no ecrã login
    Then escreve no ecrã "Sem ligação à internet!" - 1

  @tag2
  Scenario: AT2- Utilizador não insere nada no email
    Given a aplicação está pronta
    When clica no botão "Login" no ecrã login
    Then escreve no ecrã "Introduza o seu email." - 2

  @tag3
  Scenario: AT3- Utilizador não insere nada na palavra-passe
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And clica no botão "Login" no ecrã login
    Then escreve no ecrã "Introduza a sua palavra-passe." - 3

  @tag4
  Scenario: AT4- Email inserido não é válido
    Given a aplicação está pronta
    When utilizador insere "NotValid" no campo de email
    And utilizador insere "XPTO1" no campo de palavra-passe
    And clica no botão "Login" no ecrã login
    Then escreve no ecrã "O email não é válido!" - 4

  @tag5
  Scenario: AT5- Email inserido não está registado
    Given a aplicação está pronta
    When utilizador insere "teste@teste.pt" no campo de email
    And utilizador insere "XPTO1" no campo de palavra-passe
    And clica no botão "Login" no ecrã login
    Then escreve no ecrã "O email inserido não está registado!" - 5

  @tag6
  Scenario: AT6- Palavra-passe inserida está incorreta
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "NotValid" no campo de palavra-passe
    And clica no botão "Login" no ecrã login
    Then escreve no ecrã "A palavra-passe inserida está incorreta!" - 6

  @tag7
  Scenario: AT7- Conta está bloqueada
    Given a aplicação está pronta
    When utilizador insere "anar@mail.com" no campo de email
    And utilizador insere "123456789" no campo de palavra-passe
    And clica no botão "Login" no ecrã login
    Then escreve no ecrã "A sua conta foi bloqueada, por favor contacte o administrador!" - 7

  @tag8
  Scenario: AT8- Sem permissões nesta aplicação
    Given a aplicação está pronta
    When utilizador insere "tomasn@happyguest.pt" no campo de email
    And utilizador insere "123456789" no campo de palavra-passe
    And clica no botão "Login" no ecrã login
    Then escreve no ecrã "Não tem permissões para aceder a esta aplicação." - 8

  @tag9
  Scenario: AT7- Utilizador efetua o Login com sucesso
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "XPTO1" no campo de palavra-passe
    Then é apresentada a página "Home"
