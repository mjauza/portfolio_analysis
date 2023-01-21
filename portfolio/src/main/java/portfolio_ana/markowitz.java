package portfolio_ana;

import org.apache.commons.math3.linear.RealMatrix;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class markowitz {

    public static void get_optim_port(RealMatrix ret_cov, double[] ret_mean, String[] stock_names, double exp_ret){

        Primitive64Matrix H = Primitive64Matrix.FACTORY.rows(ret_cov.getData());

        int num_stocks = ret_mean.length;
        //final Variable[] tmpVariables = new Variable[num_stocks];
        List<Variable> variables = new ArrayList<>();
        for(int i=0; i<num_stocks; i++){
            String var_name = stock_names[i];
            //tmpVariables[i] = Variable.make(var_name).lower(null);
            variables.add(Variable.make(var_name));
        }
        final ExpressionsBasedModel retVal = new ExpressionsBasedModel(variables);
        final Expression tmp100P = retVal.addExpression("Balance");
        for (final Variable tmpVariable : variables) {
            tmp100P.set(tmpVariable, BigDecimal.ONE);
        }
        tmp100P.level(BigDecimal.ONE);
        final Expression mean_exp = retVal.addExpression("ExpectRet");
        for (int i= 0; i<num_stocks; i++){
            mean_exp.set(variables.get(i), ret_mean[i]);
        }
        mean_exp.level(exp_ret);
        final Expression tmpVar = retVal.addExpression("Variance");
        tmpVar.setQuadraticFactors(variables, H.divide(2));
        tmpVar.weight(BigDecimal.ONE);

        Optimisation.Result tmpResult = retVal.minimise();

        final Optimisation.State tmpState = tmpResult.getState();

        double opt_var = 2*tmpResult.getValue();
        double[] opt_positions = new double[num_stocks];
        for(int i=0; i<num_stocks; i++){
            opt_positions[i] = tmpResult.doubleValue(i);
        }

        System.out.println(tmpState);
        System.out.println("Optimal variance " + opt_var);
        for(int i = 0; i<num_stocks; i++){
            System.out.println("Stock " + stock_names[i] + " = " + opt_positions[i]);
        }





    }
}
