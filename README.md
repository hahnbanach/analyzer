[![CI](https://github.com/hahnbanach/analyzer/actions/workflows/ci.yml/badge.svg)](https://github.com/hahnbanach/analyzer/actions/workflows/ci.yml)
[![License: GPL v2](https://img.shields.io/badge/License-GPL_v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
[![Scala](https://img.shields.io/badge/Scala-2.13%20%7C%203-DC322F.svg?logo=scala&logoColor=white)](https://www.scala-lang.org/)
[![Project status](https://img.shields.io/badge/status-active-brightgreen.svg)](#status)

# Analyzer language and functions

The analyzers are a Domain Specific Language which allow to put together various functions using logical operators.

Through the analyzers, you can leverage on various NLP algorithms included in StarChat and combine the results of those algorithms with other rules. For instance you might want to get into the state which asks for the email only if a variable "email" is not set. Or you want to escalate to a human operator after you detect swearing for three times. Or you want to escalate only on working days. You can do all that with the analyzers.

Check [StarChat Documentation](https://getjenny.github.io/starchat-doc/#analyzer) for further details.
