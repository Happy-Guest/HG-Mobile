@tag
Feature: Criar Conta
  Como: Cliente
  Quero: Criar uma conta na aplicação mobile
  Para: Obter uma conta com as minhas informações

  @tag1
  Scenario: AT1- Utilizador não tem internet
    Given a aplicação está pronta sem internet
    When clica no botão "Registar" no ecrã login
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Sem ligação à internet!" - 1

  @tag2
  Scenario: AT2 - Utilizador não insere nada no nome
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Introduza o seu nome." - 9

  @tag3
  Scenario: AT3 - Nome inserido é muito pequeno
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XP" no campo do nome
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O nome é demasiado curto!" - 10

  @tag4
  Scenario: AT4 - Utilizador não insere nada no email
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Introduza o seu email." - 2

  @tag5
  Scenario: AT5 - Email inserido não é válido
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "NotValid" no campo de email
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O email não é válido!" - 4

  @tag6
  Scenario: AT6 - Número de telefone inserido não é válido
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "91" no campo de nº telefone
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O Nº telefone não é válido!" - 11

  @tag7
  Scenario: AT7 - Utilizador não insere nada na palavra-passe
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Introduza a sua palavra-passe." - 3

  @tag8
  Scenario: AT8 - Palavra-passe inserida é muito pequena
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPT" no campo de palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "A palavra-passe é demasiado curta!" - 12

  @tag9
  Scenario: AT9 - Utilizador não insere nada na confirmação da palavra-passe
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPTO1" no campo de palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Confirme a sua palavra-passe." - 13

  @tag10
  Scenario: AT10 - Palavra-passe inserida não corresponde à confirmação
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPTO1" no campo de palavra-passe
    And utilizador insere "XPT" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "A confirmação não corresponde!" - 14

  @tag11
  Scenario: AT11 - Email inserido já se encontra registado
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "anar@mail.com" no campo de email
    And utilizador insere "923456789" no campo de nº telefone
    And utilizador insere "XPTO1" no campo de palavra-passe
    And utilizador insere "XPTO1" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O email já se encontra registado!" - 15

  @tag12
  Scenario: AT12 - Utilizador cria a Conta com sucesso
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPTO1" no campo de palavra-passe
    And utilizador insere "XPTO1" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then é apresentada a página "Login"
