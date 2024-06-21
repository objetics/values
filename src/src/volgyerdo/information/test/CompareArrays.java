/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information.test;

import volgyerdo.commons.string.StringUtils;
import volgyerdo.information.Info;
import volgyerdo.information.SSMInfo;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class CompareArrays {

    public static void main(String[] args) {
        double minDiff = Double.MAX_VALUE;
        Info SSM = new SSMInfo();
        for (int i = 0; i < 10000; i++) {
            String X = StringUtils.randomString(100, "01");
            String Y = StringUtils.randomString(100, "01");
            double xInfo = SSM.info(X);
            double yInfo = SSM.info(Y);
            double xyInfo = SSM.info(X + Y);
            double diff = Math.abs((xInfo + yInfo) - xyInfo);
            if (diff < minDiff) {
                minDiff = diff;
                System.out.println("\n" + X + "\n" + Y + " (" + xInfo + ", " + yInfo + ", " + minDiff + ")\n");
            }
        }
    }

}
