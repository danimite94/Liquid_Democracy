package Firebase_Cloud_Messaging_ADAPTER;

import java.util.List;

public class MyResponse {

    public int success;
    public long multicast_id;
    public int failure;
    public int canonical_ids;
    public List<Result> results;

    public MyResponse() {
    }

    public MyResponse(int success, long multicast_id, int failure, int canonical_ids, List<Result> results) {
        this.success = success;
        this.multicast_id = multicast_id;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
