(defproject suan "0.0.0"
  :description "A suan implementation based on SKI"
  :url ""
  :license {:name "Unlicense"
            :url "https://unlicense.org"}

  :repositories [["bintray" "https://jcenter.bintray.com/"]]

  :dependencies [[org.clojure/clojure "1.10.1"]

                 [org.clojure/tools.logging "1.0.0"]
                 [org.slf4j/slf4j-api "1.7.30"]
                 [log4j/log4j "1.2.17"]

                 [org.junit.jupiter/junit-jupiter "5.4.2" :scope "tests"]]

  :jvm-opts ["-Xms1g" "-Xmx5g" "-server"]
  :javac-options ["--release" "14" "--enable-preview" "-Xlint:preview"]
  :omit-source true

  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java" "src/tests/java/"]
  :resource-paths ["src/main/resources"]
  :test-paths ["src/tests/java/"]
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}

  :jar-name "suan.jar"
  :uberjar-name "suan-standalone.jar"

  :aot :all
  :main suan.main)
