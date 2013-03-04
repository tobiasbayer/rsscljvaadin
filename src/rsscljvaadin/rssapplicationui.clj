(ns rsscljvaadin.rssapplicationui
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
           ItemClickEvent$ItemClickListener]
          [com.vaadin.data.util
           IndexedContainer])
  (require [rsscljvaadin.rss :as rss])
  (:gen-class
   	:name rsscljvaadin.RSSApplicationUI
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
  [content-label link-label]
  (reify ItemClickEvent$ItemClickListener
    (itemClick
     [_ evt]
     (.setValue content-label (.getValue (.getItemProperty (.getItem evt) "Description")))
     (.setValue link-label (.getValue (.getItemProperty (.getItem evt) "Link"))))))

(defn- add-action
  [button action]
  (.addListener button (create-button-click-listener action)))

(defn- create-button
  [caption action]
  (doto (Button. caption) (add-action action)))

(defn- create-feed-table
  [content-label link-label]
  (doto (Table.)
    (.setWidth "100%")
    (.setHeight "50%")
    (.setSelectable true)
    (.setImmediate true)
    (.addItemClickListener (create-item-click-listener content-label link-label))))

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

(defn- create-container
  [items]
  (let [c (IndexedContainer.)]
    (.addContainerProperty c "Title" String nil)
    (.addContainerProperty c "Link" String nil)
    (.addContainerProperty c "Description" String nil)
    (doseq [item items]
      (let [i (.addItem c (Object.))]
        (.setValue (.getItemProperty i "Title") (:title item))
        (.setValue (.getItemProperty i "Link") (:link item))
        (.setValue (.getItemProperty i "Description") (:description item))))
    c))

(defn- fetch-feed
  [url table]
  (.setContainerDataSource table (create-container (rss/fetch-feed url)) (java.util.ArrayList. ["Title"])))

(defn- create-link-label
  []
  (Label.))

(defn- create-main-layout
  []
  (let [content-label (create-feed-content-label)
        url-field (create-url-field)
        link-label (create-link-label)
        feed-table (create-feed-table content-label link-label)]
  	(doto (VerticalLayout.)
    	(.setMargin true)
    	(.setSpacing true)
      (.addComponent (doto (HorizontalLayout.)
                       (.setWidth "100%")
                       (.addComponent url-field)
                       (.addComponent (create-button "Fetch" #(fetch-feed (.getValue url-field) feed-table)))))
	    (.addComponent feed-table)
    	(.addComponent content-label)
      (.addComponent link-label))))

(defn -init
  [main-ui request]
  (doto main-ui (.setContent (create-main-layout))))