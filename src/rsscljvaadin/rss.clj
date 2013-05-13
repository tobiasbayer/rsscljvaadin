(ns rsscljvaadin.rss
  (:require [clojure.xml :as xml]))

(defn- filter-tag
  [content tag]
  (filter #(= (:tag %) tag) content))

(defn- node-value 
  [node]
  (first (:content node)))

(defn- subnode
  [node tag]
  (first (filter-tag (:content node) tag)))
 
(defn- title 
  [item]
  (node-value (subnode item :title)))

(defn- link 
  [item]
  (node-value (subnode item :link)))

(defn- description 
  [item]
  (node-value (subnode item :description)))

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