# Workflow script to deploy to staging and production

node ("vagrant") {

    //STAGING
    stage 'staging'
    git url:'git@github.com:puppetlabs/cloudbees-site',branch:'staging'
    def sut_host = 'wordpress-staging.pdx.puppetlabs.demo'
    def VAGRANT_PREFIX = "cd ~/projects/cloudbees-demo; "
    def SUT_PREFIX = "${VAGRANT_PREFIX} vagrant ssh ${sut_host} -c 'facter ipaddress_enp0s8'"
    sh "${VAGRANT_PREFIX} vagrant ssh /master/ -c 'sudo -i r10k deploy environment -p'"
    sh "${VAGRANT_PREFIX} vagrant provision ${sut_host}"
    checkpoint("staging_applied")
    sh "WORDPRESS_HOST=`${SUT_PREFIX}` rspec spec/"
    checkpoint("staging_complete")
    
    input "Apply to production?"
    
    //PRODUCTION
    stage 'production'
    sh "git co production"
    sh "git merge origin/staging"
    sh "git push origin HEAD"
    sh "${VAGRANT_PREFIX} vagrant ssh /master/ -c 'sudo -i r10k depploy environment -p"
    
    def prod_host = 'wordpress.pdx.puppetlabs.demo'
    sh "${VAGRANT_PREFIX} vagrant provision ${prod_host}"
}


