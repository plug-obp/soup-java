import soup.fx.SoupView;

module obp.soup.fx {
    requires javafx.controls;
    requires obp.fx.objectbrowser.api;
    requires obp.fx.objectbrowser;
    requires obp.soup.core;
    requires java.desktop;
    provides obp3.fx.objectbrowser.api.ObjectView with SoupView;
    exports soup.fx;
}