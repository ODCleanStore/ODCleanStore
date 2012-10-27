package cz.cuni.mff.odcleanstore.engine.db.model;

import java.util.ArrayList;

/**
 * Model class representing a group rule.
 * @author Petr Jerman
 */
public class GroupRule {
    public int transformerInstanceId;
    public int groupId;

    public static Integer[] selectDeepClone(GroupRule[] src, int transformerInstanceId) {
        if (src == null) {
            return new Integer[0];
        }

        ArrayList<Integer> dst = new ArrayList<Integer>();
        for (int i = 0; i < src.length; i++) {
            if (src[i].transformerInstanceId == transformerInstanceId) {
                dst.add(new Integer(src[i].groupId));
            }
        }
        return dst.toArray(new Integer[0]);
    }
}
