package logion.backend.api.view;

public class CreateTokenRequestView {

    private final String tokenName;
    private final String userAccount;
    private final int numberOfGoldBars;

    public CreateTokenRequestView(String tokenName, String userAccount, int numberOfGoldBars) {
        this.tokenName = tokenName;
        this.userAccount = userAccount;
        this.numberOfGoldBars = numberOfGoldBars;
    }

    @Override
    public String toString() {
        return "TokenRequestCreationInput{" +
                "tokenName='" + tokenName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", numberOfGoldBars=" + numberOfGoldBars +
                '}';
    }
}
