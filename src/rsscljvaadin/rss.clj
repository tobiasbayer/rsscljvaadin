(ns rsscljvaadin.rss
  (:require [clojure.xml :as xml]))

(defn- filter-tag
  [tag coll]
  (filter #(= (:tag %) tag) (:content coll)))

(defn- extract-content
  [coll]
  (map #(:content %) coll))

(defn- extract-tag-content
  [tag coll]
  (first (flatten (extract-content (filter-tag tag coll)))))

 (defn- channel
  [feed]
  (first (:content feed)))

(defn- items
  [channel]
  (filter-tag :item channel))

(defn- title
  [item]
  (extract-tag-content :title item))

(defn- description
  [item]
  (extract-tag-content :description item))

(defn- link
  [item]
  (extract-tag-content :link item))

(defn fetch-feed
  [url]
  (map #(hash-map :title (title %) :link (link %) :description (description %)) (items (channel (xml/parse url)))))


;TODO remove this function
(defn -main
  []
  (doseq [item (items (channel "http://www.spiegel.de/schlagzeilen/tops/index.rss"))]
          (println (title item))
      		(println (link item))
      	  (println (description item))))