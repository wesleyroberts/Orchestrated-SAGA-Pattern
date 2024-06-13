

# Orchestrated-SAGA-Pattern

**Tópicos que iremos abordar**
- Orchestrated saga parttern
- transactions
- Event Driven Architecture
- spring boot
- docker
- kafka

Este repositório tem como objetivo o estudo sobre o padrão SAGA  Orquestrado.

Tudo começa através de microsserviços, que são uma solução arquitetural e organizacional do desenvolvimento de software na qual o software consiste em pequenos serviços independentes que se comunicam usando APIs.

**Solucionam**

- delimitação por domínio de negócio
- Escalabilidade e alta disponibilidade
- Entrega contínua
- Resiliência e tolerância a falhas
- baixo acoplamento

**Desafios**

- Forte necessidade de governança
- Aumento da complexidade do sistema
- Maiores pontos de falha
- Grande necessidade de métricas, observabilidade e rastreamento

**Obeservações**

> *É interessante cada microsserviço possuir seu **próprio banco de dados** (não é obrigatório), porém é uma boa prática, é excelente para o isolamento do serviço, porém, traz o problema para gerenciar as **transações distribuídas**.*
>

Caso a arquitetura **não seja devidamente planejad**a, ou uma trasação distribuída não seja **devidamente tratada,** pode gerar inconsistências de dados.

## Transações Distribuídas

São transaçẽos que **iniciam em um microsserviço** e é **finalizada em outro** de maneira **conceitual**.

A trasanção não é concreta como uma transação de banco de dados, é apenas uma simbologia no fluxo de transação.  

O conceito de uma transação distribuída então é aplicada a um **contexto**, ou seja, um fluxo que é iniciado e que altera o estado dos microsseviços e que, embora não estjam na mesma transação atômica, pertencem ao mesmo contexto.

### Tow-Phase Commit Protocol - Antigo

2PC, ou Tow-Phase Commit Protocol, que é um **protocolo atômico**, ou seja, elabora uma estratégia para realizar um **commit em duas etapas em uma trasação distrubuída**. 

Geralmente ocorrerá de maneira síncrona, com requisições HTTP, essa estratégia visa iniciar o seguinte fluxo: 

- Fase 1 = realiza requisições para os serviços envolvidos para validar se o serviço tem o necessário para persistir uma informação.
- Fase 2 = realiza uma segunda requisição para cada serviço, persistindo a informação desejada.

O problema é que esse caminho, em **qualquer cenário de falha**, os **dados ficam incosistentes**.

Outro probelma é que se um dos serviços tem uma latência muito alta ou esteja inválido, isso irá gerar um atraso ou impossibilidade de continuar o fluxo. 

## Padrão Saga - Conceitos

É um padrão de projetos que tem como objetivo garantir a execução de sucesso de um fluxo de transações e também garantir que em caso de falha, todas as alterações sejam desfeitas na sequência que foram realizadas.

Existem duas implementações a **Orquestrada** e o **Coreografado**.

Benefícios do padrão Saga:

- O padrão saga demanda uma comlexidade de implementação, porém, resolve os porblemas de inconsistência de dados entre serviços.
- Não utiliza estratégia de 2PC e funciona de maneira assíncrona.

### Orquestrada

Nesta implementação um agente externo fica responsável por orquestrar, isto é, determinar qual será a ordem de envio dos eventos baseados em casos de sucesso e falha.

Nesta estratégia, apenas o orquestrador deve conhecer os serviços, além disso os serviços envolvidos devem ser totalmente independentes e não ter conhecimento algum de qual será o próximo ou anterior chamado.

**Recomendação:**

> *Utilizar quando já existe uma arquitetura de microsserviços que ainda não utiliza o padrão saga e que possua vários serviçõs implementados, pois acopla toda a lógica de orquestração em apenas um local.*
> 

O Orquestrador tem como obrigação garantir o rollback de todos os serviços em caso de uma falha.

**Vantagens**

1. Toda lógica fica centralizada, diminuindo a possibilidade de falhas em relação à orquestração dos eventos.
2. Maior testabilidade dos fluxos.
3. Os microsserviços envolvidos são totalmente independentes.
4. Recomendado para ser implementado em arquiteturas já existentes e com grandes fluxos de transações distribuídas.

![Saga Orchestrated schema](imagens/SagaOrchestrated.png)

### Coreografada

Seu conceito é criar uma coreografia existente entre os microsserviços, ou seja, o próprio serviço possui a lógica para saber qual será o próximo passo a ser decidido no fluxo com base no resultado da iteralção atual, seja sucesso ou falha.

Aqui os microsserviçõs tem conhecimento dos próximo serviços que serão executados.

Essa implementação é recomendad para arquitetura que está ainda em estágio inicial de desenvolvimento, evita a necessidade de manter mais um serviço de orquestração.

O próprio serviço deve garantir o próprio rollback e deve garantir que será feito o rollback.

**vantagens**

- Excelente para arquiteturas ou fluxo de transações pequenas, que nãi envolvam muitos serviços.
- É preferencial que seja impementado caso a arquitetura esteja nascendo, onde os fluxos ainda não estão definidos e envolvendo vários serviços.

**Desvantagens**

- Maior complexidade para entender e manter a lógica conforme ocorra um grande crescimento do fluxo das transações.
- Os microsseviços não são independentes, eles precisam conhecer quem vem antes ou depois no fluxo de execução.
- Pode ser mais compexo de ser testado devido o conhecimento geral dos microsserviços envolvidos.

![Saga Orchestrated schema](imagens/Saga%20Choreografy.png)


## Saga Execution
Ambas as implementaçõs do Saga acima utilizam o conceito chamado de **Saga Execution Contorller** ou **SEC**, que é um objeto ou entidade controloadora de execução do saga.

**Observação**
> Caso estejam **implementando o orquestrado**, o Sec será o próprio orquestrador, ou seja, o serviço como um todo, responsável por enviar e receber eventos com base em seu estado atual.

> caso esteja **implementado o coreografado**, o sec será implementado em cada serviço que faz parte do fluxo do saga, sendo um objeto, uma classe contendo a lógica, ou até um objeto externo, contanto que ele apenas redirecione os eventos, mantendo a lógica de conhecimento da ordem de execução.

![SEC](imagens/SEC.png)

## EDA - Event Driven Architeture

A EDA não possui uma implementação física, ela é um desenho arquitetural,ou seja, você deve implementar sua arquitetura usando o modelo da EDA.

O padrão saga visto anteriormente e o Outbox não são conceitos da EDA, e sim padrões que tratam problemas envolvendo transações distribuídas. Nem todo projeto que utiliza ferramentas de eventos como kafka e RabbitMQ podem ser considerados um EDA, eles precisam estar dentro do modelo arquitetura do EDA.

Menssage Brokers como Apache Kafka e RabbitMQ são ferramentas que contribuem para a implatação desta arquitetura.


 

## kafka [<img src="imagens/kafka.png" width="20"/>](/imagens/kafka.png) 



Esta ferramenta é uma plataforma de streaming de eventos distribuída, além de ser código aberto, ele pode ser usado para pipelines de dados de alto desempenho, análise de streaming, integração de dados e aplicativos de missão crítica.

Por mais que comparemos ele com o RabbitMQ ele vai além de ser apenas um menssage broker, ele é um ecossitema de streaming de eventos de maneira distribuída.

Ele trabalha com o conceito de **publisher** e **subscriber**, **particionamento**, **grupos de consumo** e etc.

Conceitos dos Kafka
- **Tópico:** canal de comunicação de eventos, depois de darmosu um nome, os produtores publicam eventos e os consumidores as consomem.
- **Producer:** responsável por publicar eventos em um ou mais tópicos.
- **Consumer:** respnsável por consumir evenetos de um ou mais tópicos e processá=los de acordo com a lógica de negócio do sistema.
- **Consumer group:** conjunto de consumidores que trabalham juntos para consumir eventos de um ou mais tópicos de forma coordenada, permitindo uma maior escalabilidade e tolerância a falhas na leitura de eventos do kafka.