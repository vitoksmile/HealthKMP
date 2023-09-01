Pod::Spec.new do |spec|
    spec.name                     = 'HealthKMPSample'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'https://github.com/vitoksmile/HealthKMP'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Wrapper for HealthKit on iOS and Google Fit and Health Connect on Android.'
    spec.vendored_frameworks      = 'build/cocoapods/framework/HealthKMPSample.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.1'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':sample',
        'PRODUCT_MODULE_NAME' => 'HealthKMPSample',
    }
                
    spec.script_phases = [
        {
            :name => 'Build HealthKMPSample',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end