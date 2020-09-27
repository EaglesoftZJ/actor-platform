package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import im.actor.runtime.bser.*;
import im.actor.runtime.collections.*;
import static im.actor.runtime.bser.Utils.*;
import im.actor.core.network.parser.*;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.j2objc.annotations.ObjectiveCName;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import im.actor.core.api.*;

public class ResponseInviteList extends Response {

    public static final int HEADER = 0xa04;
    public static ResponseInviteList fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseInviteList(), data);
    }

    private List<ApiInviteState> invites;
    private List<ApiUser> relatedUsers;
    private List<ApiGroup> relatedGroups;
    private List<ApiTeam> relatedTeams;

    public ResponseInviteList(@NotNull List<ApiInviteState> invites, @NotNull List<ApiUser> relatedUsers, @NotNull List<ApiGroup> relatedGroups, @NotNull List<ApiTeam> relatedTeams) {
        this.invites = invites;
        this.relatedUsers = relatedUsers;
        this.relatedGroups = relatedGroups;
        this.relatedTeams = relatedTeams;
    }

    public ResponseInviteList() {

    }

    @NotNull
    public List<ApiInviteState> getInvites() {
        return this.invites;
    }

    @NotNull
    public List<ApiUser> getRelatedUsers() {
        return this.relatedUsers;
    }

    @NotNull
    public List<ApiGroup> getRelatedGroups() {
        return this.relatedGroups;
    }

    @NotNull
    public List<ApiTeam> getRelatedTeams() {
        return this.relatedTeams;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiInviteState> _invites = new ArrayList<ApiInviteState>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _invites.add(new ApiInviteState());
        }
        this.invites = values.getRepeatedObj(1, _invites);
        List<ApiUser> _relatedUsers = new ArrayList<ApiUser>();
        for (int i = 0; i < values.getRepeatedCount(2); i ++) {
            _relatedUsers.add(new ApiUser());
        }
        this.relatedUsers = values.getRepeatedObj(2, _relatedUsers);
        List<ApiGroup> _relatedGroups = new ArrayList<ApiGroup>();
        for (int i = 0; i < values.getRepeatedCount(3); i ++) {
            _relatedGroups.add(new ApiGroup());
        }
        this.relatedGroups = values.getRepeatedObj(3, _relatedGroups);
        List<ApiTeam> _relatedTeams = new ArrayList<ApiTeam>();
        for (int i = 0; i < values.getRepeatedCount(4); i ++) {
            _relatedTeams.add(new ApiTeam());
        }
        this.relatedTeams = values.getRepeatedObj(4, _relatedTeams);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.invites);
        writer.writeRepeatedObj(2, this.relatedUsers);
        writer.writeRepeatedObj(3, this.relatedGroups);
        writer.writeRepeatedObj(4, this.relatedTeams);
    }

    @Override
    public String toString() {
        String res = "response InviteList{";
        res += "invites=" + this.invites;
        res += ", relatedUsers=" + this.relatedUsers;
        res += ", relatedGroups=" + this.relatedGroups;
        res += ", relatedTeams=" + this.relatedTeams;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
