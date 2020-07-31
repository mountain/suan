(ns suan.main
  (:import suan.SuanSystem)
  (:require [clojure.tools.logging :as log])
  (:gen-class))

(defn -main []
  (SuanSystem/main (make-array java.lang.String 0)))
