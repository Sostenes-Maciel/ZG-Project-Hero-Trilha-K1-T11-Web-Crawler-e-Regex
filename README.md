# ANS TISS Web Crawler

Crawler/Web Scraper desenvolvido em Groovy para coletar informações públicas disponibilizadas pela Agência Nacional de Saúde Suplementar (ANS), relacionadas ao padrão TISS (Troca de Informação de Saúde Suplementar).

O projeto utiliza requisições HTTP, parsing de HTML e expressões regulares para navegar pelo site da ANS, localizar documentos e salvar os resultados automaticamente.

## Objetivo

O projeto foi desenvolvido para realizar as seguintes tarefas:

### Task 1 — Componente de Comunicação

O crawler acessa o site da ANS e navega automaticamente até a página do padrão TISS.

Em seguida:

* Localiza a versão mais recente disponível;
* Identifica a tabela de arquivos do padrão TISS;
* Localiza o **Componente de Comunicação**;
* Obtém o endereço do arquivo diretamente do HTML;
* Realiza o download do arquivo `.zip`.

O arquivo é salvo em:

```text
Downloads/Aquivos_padrao_TISS/
```

### Task 2 — Histórico das versões

O crawler acessa a página de histórico das versões dos componentes do padrão TISS e realiza o parsing da tabela.

São coletados:

* Competência;
* Publicação;
* Início de Vigência.

Os registros são filtrados a partir da competência **jan/2016** e armazenados em um arquivo CSV.

O arquivo é salvo em:

```text
Downloads/Historico_TISS/historico_tiss.csv
```

As datas são padronizadas no formato:

```text
dd/MM/yyyy
```

### Task 3 — Tabela de erros

O crawler acessa a seção de **Tabelas relacionadas**, localiza a **Tabela de erros no envio para a ANS** e realiza o download do arquivo disponibilizado pela ANS.

O arquivo é salvo em:

```text
Downloads/Tabelas_relacionadas/
```

## Tecnologias utilizadas

* **Groovy**
* **Gradle**
* **JDK 21 (Zulu)**
* **HTTPBuilder-NG** — requisições HTTP
* **Jsoup** — parsing e análise do HTML
* **Regex** — identificação de competências e links
* **CSV** — armazenamento dos dados do histórico

## Estrutura do projeto

```text
ans-tiss-crawler/
│
├── Downloads/
│   ├── Aquivos_padrao_TISS/
│   │   └── Componente de Comunicação (.zip)
│   │
│   ├── Historico_TISS/
│   │   └── historico_tiss.csv
│   │
│   └── Tabelas_relacionadas/
│       └── Tabela de erros (.xlsx)
│
├── src/
│   └── main/
│       └── groovy/
│           └── br/
│               └── com/
│                   └── zg/
│                       └── crawler/
│                           ├── Main.groovy
│                           │
│                           ├── model/
│                           │   └── VersaoTiss.groovy
│                           │
│                           ├── service/
│                           │   ├── HttpService.groovy
│                           │   └── TissCrawler.groovy
│                           │
│                           └── util/
│                               ├── CsvWriter.groovy
│                               └── Downloader.groovy
│
├── build.gradle
└── settings.gradle
```

## Organização das classes

### `Main`

Ponto de entrada da aplicação. Sua responsabilidade é apenas iniciar o crawler e executar as Tasks.

### `TissCrawler`

Contém a lógica principal do crawler, como:

* navegação entre as páginas da ANS;
* localização dos links;
* parsing das tabelas;
* filtragem dos dados;
* execução das Tasks.

### `HttpService`

Responsável pelas requisições HTTP utilizando o HTTPBuilder-NG.

### `Downloader`

Responsável por realizar os downloads dos arquivos encontrados pelo crawler.

### `CsvWriter`

Responsável por gerar o arquivo CSV com os dados do histórico do TISS.

### `VersaoTiss`

Modelo utilizado para representar os dados coletados da tabela de histórico.

## Fluxo do crawler

O funcionamento geral do projeto segue o seguinte fluxo:

```text
Site da ANS
    ↓
Espaço do Prestador de Serviços de Saúde
    ↓
TISS
    ↓
Página da versão atual
    ↓
Localização dos arquivos / tabelas
    ↓
Extração dos links e dados
    ↓
Download / CSV
```

O crawler procura os links diretamente no HTML das páginas, evitando deixar as URLs dos arquivos finais fixas no código.

## Como executar

### Pré-requisitos

* JDK 21;
* IntelliJ IDEA;
* Gradle;
* Conexão com a internet.

### Execução pelo IntelliJ

Abra o projeto no IntelliJ e execute a classe:

```text
Main.groovy
```

As três Tasks são chamadas pelo `Main`.

Exemplo:

```groovy
def crawler = new TissCrawler()

crawler.executarTask1()
crawler.executarTask2()
crawler.executarTask3()
```

Após a execução, os arquivos coletados estarão dentro da pasta `Downloads`.

## Dependências

As principais dependências utilizadas no projeto são:

```groovy
implementation 'io.github.http-builder-ng:http-builder-ng-core:1.0.4'
implementation 'org.jsoup:jsoup:1.23.2'
implementation 'org.codehaus.groovy:groovy:3.0.25'
implementation 'org.codehaus.groovy:groovy-xml:3.0.25'
```

## Resultados

Ao executar o crawler, são gerados:

```text
Downloads/
├── Aquivos_padrao_TISS/
│   └── Componente de Comunicação (.zip)
│
├── Historico_TISS/
│   └── historico_tiss.csv
│
└── Tabelas_relacionadas/
    └── Tabela de erros (.xlsx)
```

## Observação

Os dados coletados pelo projeto são documentos e informações disponibilizados publicamente pela ANS.

O crawler foi desenvolvido com objetivo acadêmico, utilizando os conceitos estudados de requisições HTTP, parsing de HTML, Regex, organização de classes e manipulação de arquivos.

## Autor 

Sóstenes Marques Maciel