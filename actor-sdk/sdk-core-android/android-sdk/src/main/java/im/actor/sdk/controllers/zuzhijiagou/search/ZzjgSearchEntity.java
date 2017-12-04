/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.sdk.controllers.zuzhijiagou.search;

import com.google.j2objc.annotations.Property;

import java.io.IOException;

import im.actor.core.entity.Avatar;
import im.actor.core.entity.Peer;
import im.actor.core.entity.SearchEntity;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;
import im.actor.runtime.storage.ListEngineItem;
import im.actor.sdk.controllers.root.Node;

public class ZzjgSearchEntity extends SearchEntity implements ListEngineItem {

    public static final BserCreator<ZzjgSearchEntity> CREATOR = new BserCreator<ZzjgSearchEntity>() {
        @Override
        public ZzjgSearchEntity createInstance() {
            return new ZzjgSearchEntity();
        }
    };

    private Node node;

    public ZzjgSearchEntity(Peer peer, long order, Avatar avatar, String title) {
        super(peer, order, avatar, title);
    }

    public ZzjgSearchEntity(Peer peer, long order, Avatar avatar, String title, Node Node) {
        super(peer, order, avatar, title);
        this.node = node;
    }


    @Override
    public void parse(BserValues values) throws IOException {
        super.parse(values);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        super.serialize(writer);
    }

    private ZzjgSearchEntity() {
        super();
    }


    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
