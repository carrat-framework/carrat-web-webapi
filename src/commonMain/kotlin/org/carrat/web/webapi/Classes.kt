package org.carrat.web.webapi

/** Returns true if the element has the given CSS class style in its 'class' attribute */
public fun Element.hasClass(cssClass: String): Boolean = classes.matches("""(^|.*\s+)$cssClass($|\s+.*)""".toRegex())

/**
 * Adds CSS class to element. Has no effect if all specified classes are already in class attribute of the element
 *
 * @return true if at least one class has been added
 */
public fun Element.addClass(vararg cssClasses: String): Boolean {
    val missingClasses = cssClasses.filterNot { hasClass(it) }
    if (missingClasses.isNotEmpty()) {
        val presentClasses = classes.trim()
        classes = buildString {
            append(presentClasses)
            if (!presentClasses.isEmpty()) {
                append(" ")
            }
            missingClasses.joinTo(this, " ")
        }
        return true
    }

    return false
}

/**
 * Removes all [cssClasses] from element. Has no effect if all specified classes are missing in class attribute of the element
 *
 * @return true if at least one class has been removed
 */
public fun Element.removeClass(vararg cssClasses: String): Boolean {
    if (cssClasses.any { hasClass(it) }) {
        val toBeRemoved = cssClasses.toSet()
        classes = classes.trim().split("\\s+".toRegex()).filter { it !in toBeRemoved }.joinToString(" ")
        return true
    }

    return false
}

public var Element.classes: String
    get() = this.getAttribute("class") ?: ""
    set(value) {
        this.setAttribute("class", value)
    }

public var Element.classSet: Set<String>
    get() {
        return this.className.split("""\s+""".toRegex()).filter { it.isNotEmpty() }.toSet()
    }
    set(value) {
        this.className = value.joinToString(" ")
    }
