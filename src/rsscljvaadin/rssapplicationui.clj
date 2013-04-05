(ns rsscljvaadin.rssapplicationui
  (import [com.vaadin.ui 
           VerticalLayout
           HorizontalLayout
           Table
           Table$ColumnHeaderMode
           Label
           TextField
           Button 
           Button$ClickListener 
           Link]
          ;TODO check imports necessary?
          [com.vaadin.event
           ItemClickEvent$ItemClickListener]
          [com.vaadin.data.util
           IndexedContainer]
          [com.vaadin.server
           ExternalResource])
  (require [rsscljvaadin.rss :as rss])
  (:gen-class
    :name rsscljvaadin.RSSApplicationUI
    :extends com.vaadin.ui.UI))

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
     (.setCaption link-label (.getValue (.getItemProperty (.getItem evt) "Link")))
     (.setResource link-label (ExternalResource. (.getValue (.getItemProperty (.getItem evt) "Link")))))))

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
    (.setColumnHeaderMode Table$ColumnHeaderMode/HIDDEN)
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
  (doto (Link.)
    (.setTargetName "_blank")))

(defn- create-main-layout
  []
  (let [content-label (create-feed-content-label)
        url-field (create-url-field)
        link-label (create-link-label)
        feed-table (create-feed-table content-label link-label)
        fetch-button (create-button 
                      "Fetch" 
                      #(fetch-feed 
                        (.getValue url-field) 
                        feed-table))]
  	(doto (VerticalLayout.)
    	(.setMargin true)
    	(.setSpacing true)
      (.addComponent (doto (HorizontalLayout.)
                       (.setWidth "100%")
                       (.setSpacing true)
                       (.addComponent 
                        (doto url-field 
                          (.setWidth "100%")))
                       (.setExpandRatio url-field 1)
                       (.addComponent fetch-button)))
	    (.addComponent feed-table)
    	(.addComponent content-label)
      (.addComponent link-label))))

(defn -init
  [main-ui request]
  (doto main-ui (.setContent (create-main-layout))))