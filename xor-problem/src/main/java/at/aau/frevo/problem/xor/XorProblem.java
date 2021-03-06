/**
 * File: XorProblem.java
 * 
 * Copyright (C) 2019 FREVO project contributors
 *
 * Universitaet Klagenfurt licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package at.aau.frevo.problem.xor;

import java.util.SplittableRandom;
import at.aau.frevo.Problem;
import at.aau.frevo.Representation;
import at.aau.frevo.RepresentationContext;

/**
 * Simple problem that carries out a number of exclusive OR operations.
 */
public class XorProblem extends Problem {

  protected float trueValue;
  protected float falseValue;
  protected float tolerance;
  protected int operationCount;


  protected float[] inputs = new float[2];
  protected float[] output = new float[1];

  /**
   * Creates a new {@code XorProblem} instance with the specified configuration.
   * 
   * @param builder the {@code XorProblemBuilder} used for configuration
   * @param seed    the seed to use
   */
  public XorProblem(XorProblemBuilder builder, long seed) {
    super(seed);

    trueValue = builder.getTrueValue();
    falseValue = builder.getFalseValue();
    tolerance = builder.getTolerance();
    operationCount = builder.getOperationCount();
  }

  @Override
  public double evaluateRepresentation(Representation representation) {
    var evaluationRandom = new SplittableRandom(seed);
    var representationContext = representation.createContext(evaluationRandom.split());
    double errorSum = 0;
    for (int i = 0; i < operationCount; i++) {
      errorSum += checkXor(representationContext, evaluationRandom.nextBoolean(),
          evaluationRandom.nextBoolean());
    }

    // convert error to fitness and return
    return (1 - errorSum / operationCount) * 100;
  }

  /**
   * Checks that the {@code RepresentationContext} performs the specified XOR operation.
   * 
   * @param representationContext the {@code RepresentationContext} to check
   * @param input1                first input value
   * @param input2                second input value
   * @return error value, {@code 0} if correct, otherwise a value {@code 0 <= v <= 1}
   */
  public double checkXor(RepresentationContext<? extends Representation> representationContext,
      boolean input1, boolean input2) {
    // use the representation context to calculate the result
    inputs[0] = input1 ? trueValue : falseValue;
    inputs[1] = input2 ? trueValue : falseValue;
    representationContext.calculate(inputs, output);

    // compare the result against the answer
    boolean answer = input1 ^ input2;
    float answerFloat = answer ? trueValue : falseValue;

    float outputRange = Math.abs(trueValue - falseValue);
    float difference = Math.abs(answerFloat - output[0]);
    if (difference < tolerance) {
      difference = 0;
    }
    return Math.min(difference, outputRange) / outputRange;
  }

  /**
   * Gets the value used to represent {@code true}.
   * 
   * @return the true value
   */
  public float getTrueValue() {
    return trueValue;
  }

  /**
   * Gets the value used to represent {@code false}.
   * 
   * @return the false value
   */
  public float getFalseValue() {
    return falseValue;
  }

  /**
   * Gets the acceptable tolerance for the true and false values.
   * <p>
   * Any value {@code v} where {@code Math.abs(v - trueValue) < tolerance)} will be accepted as
   * {@code true}, any value {@code Math.abs(v - falseValue) < tolerance)} will be accepted as
   * {@code false}.
   * 
   * @return the tolerance
   */
  public float getTolerance() {
    return tolerance;
  }

  /**
   * Gets the number of operations to perform during evaluation.
   * 
   * @return the operation count
   */
  public int getOperationCount() {
    return operationCount;
  }
}
