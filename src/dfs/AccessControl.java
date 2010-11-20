package dfs;

public class AccessControl {
    public static final byte DEFAULT_OWNER_PERMISSION = AccessControl.getPermission("rwdgs");
    public static final byte DEFAULT_GROUP_PERMISSION = AccessControl.getPermission("r");
    public static final byte DEFAULT_OTHER_PERMISSION = AccessControl.getPermission("rg"); 
    
    public static boolean checkPermission(byte permission, char permissionType) {
        boolean result;
        switch (permissionType) {
        case 's':
            result = (permission & 0x1)==0x01; 
            break;
        case 'g':
            result = (permission & 0x2)==0x02;
            break;
        case 'd':
            result = (permission & 0x4)==0x04;
            break;
        case 'w':
            result = (permission & 0x8)==0x08;
            break;
        case 'r':
            // Read permission
            result = (permission & 0x10)==0x10;
            break;
         default:
            result = false;
            break;
        }
        
        return result;
    }
    
    public static byte getPermission(String s) {
        int result = 0;
        for(int i=0; i < s.length(); i++) {
            switch (s.charAt(i)) {
            case 's':
                result += 1;
                break;
            case 'g':
                result += 2;
                break;
            case 'd':
                result += 4;
                break;
            case 'w':
                result += 8;
                break;
            case 'r':
                result += 16;
                break;
            default:
                break;
            }
        }
        return (byte)result;
    }
}
