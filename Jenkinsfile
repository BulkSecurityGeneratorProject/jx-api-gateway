pipeline {
    agent {
      label "jenkins-maven"
    }
    environment {
      ORG               = 'kevinstl'
      APP_NAME          = 'jx-api-gateway'
      CHARTMUSEUM_CREDS = credentials('jenkins-x-chartmuseum')
    }
    stages {
      stage('CI Build and push snapshot') {
        when {
          branch 'PR-*'
        }
        environment {
          PREVIEW_VERSION = "0.0.0-SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER"
          PREVIEW_NAMESPACE = "$APP_NAME-$BRANCH_NAME".toLowerCase()
          HELM_RELEASE = "$PREVIEW_NAMESPACE".toLowerCase()
        }
        steps {
          container('maven') {
            sh "mvn -s ./.mvn/settings-custom.xml versions:set -DnewVersion=$PREVIEW_VERSION"
            sh "localedef -i en_US -f UTF-8 en_US.UTF-8"
            sh "mvn -s ./.mvn/settings-custom.xml install"
            sh 'export VERSION=$PREVIEW_VERSION && skaffold build -f skaffold.yaml'


            sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
          }

          dir ('./charts/preview') {
           container('maven') {
             sh "make preview"
             sh "jx preview --app $APP_NAME --dir ../.."
           }
          }
        }
      }
      stage('Build Release') {
        when {
          branch 'master'
        }
        steps {
          container('maven') {
            // ensure we're not on a detached head
            sh "git checkout master"
            sh "git config --global credential.helper store"

            sh "jx step git credentials"
            // so we can retrieve the version in later steps
            sh "echo \$(jx-release-version) > VERSION"
            sh "mvn -s ./.mvn/settings-custom.xml versions:set -DnewVersion=\$(cat VERSION)"
          }
          dir ('./charts/jx-api-gateway') {
            container('maven') {
              sh "make tag"
            }
          }
          container('maven') {

            sh "localedef -i en_US -f UTF-8 en_US.UTF-8"

            sh 'mvn -s ./.mvn/settings-custom.xml clean deploy'

            sh 'export VERSION=`cat VERSION` && skaffold build -f skaffold.yaml'


            sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:\$(cat VERSION)"
          }
        }
      }
      stage('Promote to Environments') {
        when {
          branch 'master'
        }
        steps {
          dir ('./charts/jx-api-gateway') {
            container('maven') {
              sh 'jx step changelog --version v\$(cat ../../VERSION)'

              // release the helm chart
              sh 'jx step helm release'

              // promote through all 'Auto' promotion Environments
              sh 'jx promote -b --all-auto --timeout 1h --version \$(cat ../../VERSION)'
            }
          }
        }
      }
    }
    post {
        always {
            cleanWs()
        }
        failure {
            input """Pipeline failed.
We will keep the build pod around to help you diagnose any failures.

Select Proceed or Abort to terminate the build pod"""
        }
    }
  }
