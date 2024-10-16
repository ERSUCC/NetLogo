// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim.etc;

import org.nlogo.core.CompilerException;
import org.nlogo.api.LogoException;
import org.nlogo.core.Syntax;
import org.nlogo.nvm.Reporter;

/**
 * A reporter that checks the syntax of NetLogo statements. This compiles code
 * precisely as _run does, so anything that this accepts should be guaranteed
 * not to produce a compiler error when it's passed to _run. However, since
 * compilation makes use of local variables and other context-sensitive
 * information, this guarantee only applies if _checksyntax and _run are used
 * in the same block of code.
 */
public final class _checksyntax
    extends Reporter {
  @Override
  public Object report(final org.nlogo.nvm.Context context) throws LogoException {
    try {
      workspace.compileForRun
          (argEvalString(context, 0), context, false);
    } catch (CompilerException error) {
      return error.getMessage();
    }
    return "";
  }


}
