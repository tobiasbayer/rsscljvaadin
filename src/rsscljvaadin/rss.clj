(ns rsscljvaadin.rss
  (:require [clojure.xml :as xml]))

(defn- filter-tag
  [content tag]
  (filter #(= (:tag %) tag) content))

(defn- node-value [node]
  (first (:content node)))

(defn- node
  [entry tag]
  (first (filter-tag (:content entry) tag)))
 
(defn- title 
  [entry]
  (node-value (node entry :title)))

(defn- link 
  [entry]
  (node-value (node entry :link)))

(defn- description 
  [entry]
  (node-value (node entry :description)))

(defn- items
  [feed]
  (filter-tag feed :item))

(defn fetch-feed
  [url]
  (map #(hash-map 
         :title (title %) 
         :link (link %) 
         :description (description %)) 
           (items (xml-seq (xml/parse url)))))