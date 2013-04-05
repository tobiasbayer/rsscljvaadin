(defproject rsscljvaadin "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.0"]
		 [com.vaadin/vaadin-server "7.0.3"]
                 [com.vaadin/vaadin-client-compiled "7.0.3"]
                 [com.vaadin/vaadin-themes "7.0.3"]
                 [javax.servlet/servlet-api "2.5"]]
  :aot [rsscljvaadin.rssapplicationui])
