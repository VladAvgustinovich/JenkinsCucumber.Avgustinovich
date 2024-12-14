node('unix') {
    stage('Git checkout') {
    checkout scm
    }
    stage('Run tests'){
        withMaven(globalMavenSettingsConfig: '', jdk: '', maven: 'Default', mavenSettingsConfig: '', traceability: true) {
         sh " mvn clean test -Dtype.browser=${type_browser} -Dtype.driver=${type_driver}  -Dcucumber.filter.tags=${test_tags}"
        }
    }
    stage('Allure') {
    allure includeProperties: false, jdk: '', results: [[path: 'target/reports/allure-results']]
    }
}