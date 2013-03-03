(ns rsscljvaadin.RSSApplicationUI
  (import [com.vaadin.ui 
           VerticalLayout
           HorizontalLayout
           Table
           Label
           TextField
           Button 
           Button$ClickListener 
           Notification]
          [com.vaadin.event
           ItemClickEvent$ItemClickListener])
  (require [rsscljvaadin.rss :as rss])
  (:gen-class
    :extends com.vaadin.ui.UI))

(defn- show-click-message
  []
  (Notification/show "Button clicked"))

(defn- create-button-click-listener 
  [action]
  (reify Button$ClickListener 
    (buttonClick 
              [_ evt]
              (action))))

(defn- create-item-click-listener
  [content-label]
  (reify ItemClickEvent$ItemClickListener
    (itemClick
     [_ evt]
     (.setValue content-label (.getValue (.getItemProperty (.getItem evt) "Description"))))))

(defn- add-action
  [button action]
  (.addListener button (create-button-click-listener action)))

(defn- create-button
  [caption action]
  (doto (Button. caption) (add-action action)))

(defn- create-feed-table
  [content-label]
  (doto (Table.)
    (.addContainerProperty "Title" String nil)
    (.addContainerProperty "Link" String nil)
    (.addContainerProperty "Description" String nil)
    (.setWidth "100%")
    (.setHeight "50%")
    ;(.setVisibleColumns (to-array ["Title" "Link"]))
    (.setSelectable true)
    (.setImmediate true)
    (.addItemClickListener (create-item-click-listener content-label))))

 (defn- create-feed-content-label
  []
  (doto (Label. "Please select a feed item in the table above" Label/CONTENT_XHTML)
    (.setWidth "100%")
    (.setHeight "50%")))

(defn- create-url-field
  []
  (doto (TextField.)
    (.setInputPrompt "Feed URL")
    (.setWidth "50%")))

(defn- fetch-feed
  [url table]
  (.removeAllItems table)
  (doseq [item (rss/fetch-feed url)]
      (.addItem table (to-array [(:title item) (:link item) (:description item)]) (Object.))))

(defn- create-main-layout
  []
  (let [content-label (create-feed-content-label)
        url-field (create-url-field)
        feed-table (create-feed-table content-label)]
  	(doto (VerticalLayout.)
    	(.setMargin true)
    	(.setSpacing true)
      (.addComponent (doto (HorizontalLayout.)
                       (.setWidth "100%")
                       (.addComponent url-field)
                       (.addComponent (create-button "Fetch" #(fetch-feed (.getValue url-field) feed-table)))))
	    (.addComponent feed-table)
    	(.addComponent content-label))))

(defn -init
  [main-ui request]
  (doto main-ui (.setContent (create-main-layout))))