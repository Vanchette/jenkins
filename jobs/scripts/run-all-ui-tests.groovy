timeout(60) {
    node('maven-slave') {
        stage('Checkout') {
            git branch: "$BRANCH", credentialsId: 'jenkins', url: 'git@github.com:Vanchette/jenkins.git'
        }
        stage('Run tests') {
            def jobs = [:]

            def runnerJobs = "$TEST_TYPE".split(",")

            jobs['ui_tests'] = {
                    node('maven-slave') {
                    stage('Ui tests on chrome') {
                        if('ui' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'ui-tests',
                                parameters: [
                                    string(name: 'BRANCH', value: BRANCH),
                                    string(name: 'BASE_URL', value: BASE_URL),
                                    string(name: 'BROWSER', value: BROWSER),
                                    string(name: 'BROWSER_VERSION', value: BROWSER_VERSION),
                                    string(name: 'GRID_URL', value: GRID_URL)
                                ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('keystone api tests')
                        }
                    }
                }
            }

            jobs['api_tests'] = {
                    node('maven-slave') {
                    stage('API tests on chrome') {
                        if('api' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'api-tests',
                                parameters: [
                                    string(name: 'BRANCH', value: BRANCH),
                                    string(name: 'BASE_URL', value: BASE_URL),
                                    string(name: 'URL', value: URL),
                                    string(name: 'USER_STATUS', value: USER_STATUS),
                                    string(name: 'EMAIL', value: EMAIL),
                                    string(name: 'ID', value: ID),
                                    string(name: 'FIRST_NAME', value: FIRST_NAME),
                                    string(name: 'LAST_NAME', value: LAST_NAME),
                                    string(name: 'PHONE', value: PHONE),
                                    string(name: 'PASSWORD', value: PASSWORD),
                                    string(name: 'USERNAME', value: USERNAME)
                                ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('keystone api tests')
                        }
                    }
                }
            }

            parallel jobs
        }
    }
}