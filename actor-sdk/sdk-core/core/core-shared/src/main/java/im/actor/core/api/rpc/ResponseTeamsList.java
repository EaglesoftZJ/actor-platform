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

public class ResponseTeamsList extends Response {

    public static final int HEADER = 0xa02;
    public static ResponseTeamsList fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseTeamsList(), data);
    }

    private List<ApiTeam> teams;

    public ResponseTeamsList(@NotNull List<ApiTeam> teams) {
        this.teams = teams;
    }

    public ResponseTeamsList() {

    }

    @NotNull
    public List<ApiTeam> getTeams() {
        return this.teams;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiTeam> _teams = new ArrayList<ApiTeam>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _teams.add(new ApiTeam());
        }
        this.teams = values.getRepeatedObj(1, _teams);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.teams);
    }

    @Override
    public String toString() {
        String res = "response TeamsList{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
