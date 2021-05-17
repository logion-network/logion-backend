package logion.backend.model;

/**
 * Well known SS58 addresses.
 */
public class DefaultAddresses {

    public static final Ss58Address ALICE = new Ss58Address("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY");

    public static final Ss58Address DEFAULT_LEGAL_OFFICER = ALICE;

    public static final Ss58Address BOB = new Ss58Address("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty");

    private DefaultAddresses() {

    }
}
