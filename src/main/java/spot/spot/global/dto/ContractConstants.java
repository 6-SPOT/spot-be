package spot.spot.global.dto;

public enum ContractConstants {
    PRIVATE_KEY("7858e77af705cc06b5674bb8e01294379c1b4737629c8498c931848b98c2a878"),
    CONTRACT_ADDRESS("0xDec5c942E7d596284a5e11C228dE0da3BEFf755c"),
    NODE_URL("https://1001.rpc.thirdweb.com"),
    DEPOSIT_METHOD_ID("0xd0e30db0"),
    TRANSFER_METHOD_ID("0x12514bba"),  // 🔥 transfer(uint256) 올바른 Method ID
    ABI_JSON(
            "[\n" +
                    "\t{\n" +
                    "\t\t\"inputs\": [],\n" +
                    "\t\t\"stateMutability\": \"nonpayable\",\n" +
                    "\t\t\"type\": \"constructor\"\n" +
                    "\t},\n" +
                    "\t{\n" +
                    "\t\t\"inputs\": [],\n" +
                    "\t\t\"name\": \"deposit\",\n" +
                    "\t\t\"outputs\": [],\n" +
                    "\t\t\"stateMutability\": \"payable\",\n" +
                    "\t\t\"type\": \"function\"\n" +
                    "\t},\n" +
                    "\t{\n" +
                    "\t\t\"inputs\": [],\n" +
                    "\t\t\"name\": \"getBalance\",\n" +
                    "\t\t\"outputs\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"internalType\": \"uint256\",\n" +
                    "\t\t\t\t\"name\": \"\",\n" +
                    "\t\t\t\t\"type\": \"uint256\"\n" +
                    "\t\t\t}\n" +
                    "\t\t],\n" +
                    "\t\t\"stateMutability\": \"view\",\n" +
                    "\t\t\"type\": \"function\"\n" +
                    "\t},\n" +
                    "\t{\n" +
                    "\t\t\"inputs\": [],\n" +
                    "\t\t\"name\": \"owner\",\n" +
                    "\t\t\"outputs\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"internalType\": \"address\",\n" +
                    "\t\t\t\t\"name\": \"\",\n" +
                    "\t\t\t\t\"type\": \"address\"\n" +
                    "\t\t\t}\n" +
                    "\t\t],\n" +
                    "\t\t\"stateMutability\": \"view\",\n" +
                    "\t\t\"type\": \"function\"\n" +
                    "\t},\n" +
                    "\t{\n" +
                    "\t\t\"inputs\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"internalType\": \"uint256\",\n" +
                    "\t\t\t\t\"name\": \"_value\",\n" +
                    "\t\t\t\t\"type\": \"uint256\"\n" +
                    "\t\t\t}\n" +
                    "\t\t],\n" +
                    "\t\t\"name\": \"transfer\",\n" +
                    "\t\t\"outputs\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"internalType\": \"bool\",\n" +
                    "\t\t\t\t\"name\": \"\",\n" +
                    "\t\t\t\t\"type\": \"bool\"\n" +
                    "\t\t\t}\n" +
                    "\t\t],\n" +
                    "\t\t\"stateMutability\": \"nonpayable\",\n" +
                    "\t\t\"type\": \"function\"\n" +
                    "\t}\n" +
                    "]"
    );

    private final String value;

    ContractConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
