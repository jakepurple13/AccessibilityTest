package com.programmersbox.accessibilitytest

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat


class GlobalActionBarService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        printStuff(event)
        printStuff("packageName: " + event.packageName)
        printStuff("className: " + event.className)
        printStuff("Source: " + event.source)
        printStuff("Source text: " + event.source?.text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            printStuff("Event source extra: " + event.source?.availableExtraData)
        }
        printStuff("Event source actionList: " + event.source?.actionList)

        //printStuff(event)

        //android:packageNames="com.samsung.safetyinformation"

        println("-".repeat(10))
    }

    fun printStuff(any: Any?) {
        Log.e("ME", "printStuff: $any")
    }

    override fun onInterrupt() {
    }

    var mLayout: FrameLayout? = null

    override fun onServiceConnected() {
        // Create an overlay and display the action bar
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.action_bar, mLayout)
        //wm.addView(mLayout, lp)
        //setupButtons()
    }

    private fun setupButtons() {
        mLayout?.findViewById<Button>(R.id.one)?.setOnClickListener {

        }
        mLayout?.findViewById<Button>(R.id.two)?.setOnClickListener {

        }
    }
}

/**
 * Utility class for sending commands to ChromeVox.
 */
object WebInterfaceUtils {
    /**
     * If injection of accessibility enhancing JavaScript screen-reader is
     * enabled.
     *
     *
     * This property represents a boolean value encoded as an integer (1 is
     * true, 0 is false).
     */
    private const val ACCESSIBILITY_SCRIPT_INJECTION = "accessibility_script_injection"

    private const val ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES =
        "ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES"

    /**
     * Direction constant for forward movement within a page.
     */
    const val DIRECTION_FORWARD: Int = 1

    /**
     * Direction constant for backward movement within a page.
     */
    const val DIRECTION_BACKWARD: Int = -1

    /**
     * Action argument to use with
     * [.performSpecialAction] to
     * instruct ChromeVox to read the currently focused element within the node.
     * within the page.
     */
    const val ACTION_READ_CURRENT_HTML_ELEMENT: Int = -1

    /**
     * Action argument to use with
     * [.performSpecialAction] to
     * instruct ChromeVox to read the title of the page within the node.
     */
    const val ACTION_READ_PAGE_TITLE_ELEMENT: Int = -2

    /**
     * Action argument to use with
     * [.performSpecialAction] to
     * instruct ChromeVox to stop all speech and automatic actions.
     */
    const val ACTION_STOP_SPEECH: Int = -3

    /**
     * Action argument to use with
     * [.performSpecialAction] to
     * instruct ChromeVox to move into or out of the special content navigation
     * mode.
     *
     *
     * Using this constant also requires specifying a direction.
     * [.DIRECTION_FORWARD] indicates ChromeVox should move into this
     * content navigation mode, [.DIRECTION_BACKWARD] indicates ChromeVox
     * should move out of this mode.
     */
    private const val ACTION_TOGGLE_SPECIAL_CONTENT = -4

    /**
     * Action argument to use with
     * [.performSpecialAction] to
     * instruct ChromeVox to move into or out of the incremental search mode.
     *
     *
     * Using this constant does not require a direction as it only toggles
     * the state.
     */
    const val ACTION_TOGGLE_INCREMENTAL_SEARCH: Int = -5

    /**
     * HTML element argument to use with
     * [.performNavigationToHtmlElementAction] to instruct ChromeVox to move to the next or previous page
     * section.
     */
    const val HTML_ELEMENT_MOVE_BY_SECTION: String = "SECTION"

    /**
     * HTML element argument to use with
     * [.performNavigationToHtmlElementAction] to instruct ChromeVox to move to the next or previous link.
     */
    const val HTML_ELEMENT_MOVE_BY_LINK: String = "LINK"

    /**
     * HTML element argument to use with
     * [.performNavigationToHtmlElementAction] to instruct ChromeVox to move to the next or previous list.
     */
    const val HTML_ELEMENT_MOVE_BY_LIST: String = "LIST"

    /**
     * HTML element argument to use with
     * [.performNavigationToHtmlElementAction] to instruct ChromeVox to move to the next or previous
     * control.
     */
    const val HTML_ELEMENT_MOVE_BY_CONTROL: String = "CONTROL"

    /**
     * Sends an instruction to ChromeVox to read the specified HTML element in
     * the given direction within a node.
     *
     *
     * WARNING: Calling this method with a source node of
     * [android.webkit.WebView] has the side effect of closing the IME
     * if currently displayed.
     *
     * @param node The node containing web content with ChromeVox to which the
     * message should be sent
     * @param direction [.DIRECTION_FORWARD] or
     * [.DIRECTION_BACKWARD]
     * @param htmlElement The HTML tag to send
     * @return `true` if the action was performed, `false`
     * otherwise.
     */
    fun performNavigationToHtmlElementAction(
        node: AccessibilityNodeInfoCompat?, direction: Int, htmlElement: String?
    ): Boolean {
        val action = if ((direction == DIRECTION_FORWARD)
        ) AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT
        else AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT
        val args = Bundle()
        args.putString(
            AccessibilityNodeInfoCompat.ACTION_ARGUMENT_HTML_ELEMENT_STRING, htmlElement
        )
        return true//PerformActionUtils.performAction(node, action, args)
    }

    fun getSupportedHtmlElements(node: AccessibilityNodeInfoCompat?): Array<String>? {
        var node = node
        val visitedNodes =
            HashSet<AccessibilityNodeInfoCompat>()

        while (node != null) {
            if (visitedNodes.contains(node)) {
                return null
            }

            visitedNodes.add(node)

            val bundle = node.extras
            val supportedHtmlElements =
                bundle.getCharSequence(ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES)

            if (supportedHtmlElements != null) {
                return supportedHtmlElements.toString().split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            }

            node = node.parent
        }

        return null
    }

    /**
     * Sends an instruction to ChromeVox to navigate by DOM object in
     * the given direction within a node.
     *
     * @param node The node containing web content with ChromeVox to which the
     * message should be sent
     * @param direction [.DIRECTION_FORWARD] or
     * [.DIRECTION_BACKWARD]
     * @return `true` if the action was performed, `false`
     * otherwise.
     */
    fun performNavigationByDOMObject(
        node: AccessibilityNodeInfoCompat?, direction: Int
    ): Boolean {
        val action = if ((direction == DIRECTION_FORWARD)
        ) AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT
        else AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT
        return true//PerformActionUtils.performAction(node, action)
    }

    /**
     * Sends an instruction to ChromeVox to move within a page at a specified
     * granularity in a given direction.
     *
     *
     * WARNING: Calling this method with a source node of
     * [android.webkit.WebView] has the side effect of closing the IME
     * if currently displayed.
     *
     * @param node The node containing web content with ChromeVox to which the
     * message should be sent
     * @param direction [.DIRECTION_FORWARD] or
     * [.DIRECTION_BACKWARD]
     * @param granularity The granularity with which to move or a special case argument.
     * @return `true` if the action was performed, `false` otherwise.
     */
    fun performNavigationAtGranularityAction(
        node: AccessibilityNodeInfoCompat?, direction: Int, granularity: Int
    ): Boolean {
        val action = if ((direction == DIRECTION_FORWARD)
        ) AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY
        else AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY
        val args = Bundle()
        args.putInt(
            AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT, granularity
        )
        return true//PerformActionUtils.performAction(node, action, args)
    }

    /**
     * Sends instruction to ChromeVox to perform one of the special actions
     * defined by the ACTION constants in this class.
     *
     *
     * WARNING: Calling this method with a source node of
     * [android.webkit.WebView] has the side effect of closing the IME if
     * currently displayed.
     *
     * @param node The node containing web content with ChromeVox to which the
     * message should be sent
     * @param action The ACTION constant in this class match the special action
     * that ChromeVox should perform.
     * @return `true` if the action was performed, `false`
     * otherwise.
     */
    fun performSpecialAction(node: AccessibilityNodeInfoCompat, action: Int): Boolean {
        return performSpecialAction(node, action, DIRECTION_FORWARD)
    }

    /**
     * Sends instruction to ChromeVox to perform one of the special actions
     * defined by the ACTION constants in this class.
     *
     *
     * WARNING: Calling this method with a source node of
     * [android.webkit.WebView] has the side effect of closing the IME if
     * currently displayed.
     *
     * @param node The node containing web content with ChromeVox to which the
     * message should be sent
     * @param action The ACTION constant in this class match the special action
     * that ChromeVox should perform.
     * @param direction The DIRECTION constant in this class to add as an extra
     * argument to the special action.
     * @return `true` if the action was performed, `false`
     * otherwise.
     */
    private fun performSpecialAction(
        node: AccessibilityNodeInfoCompat, action: Int, direction: Int
    ): Boolean {
        /*
         * We use performNavigationAtGranularity to communicate with ChromeVox
         * for these actions because it is side-effect-free. If we use
         * performNavigationToHtmlElementAction and ChromeVox isn't injected,
         * we'll actually move selection within the fallback implementation. We
         * use the granularity field to hold a value that ChromeVox interprets
         * as a special command.
         */
        return performNavigationAtGranularityAction(node, direction, action /* fake granularity */)
    }

    /**
     * Sends a message to ChromeVox indicating that it should enter or exit
     * special content navigation. This is applicable for things like tables and
     * math expressions.
     *
     *
     * NOTE: further navigation should occur at the default movement
     * granularity.
     *
     * @param node The node representing the web content
     * @param enabled Whether this mode should be entered or exited
     * @return `true` if the action was performed, `false`
     * otherwise.
     */
    fun setSpecialContentModeEnabled(
        node: AccessibilityNodeInfoCompat, enabled: Boolean
    ): Boolean {
        val direction = if ((enabled)) DIRECTION_FORWARD else DIRECTION_BACKWARD
        return performSpecialAction(node, ACTION_TOGGLE_SPECIAL_CONTENT, direction)
    }

    /**
     * Determines whether or not the given node contains web content.
     *
     * @param node The node to evaluate
     * @return `true` if the node contains web content, `false` otherwise
     */
    fun supportsWebActions(node: AccessibilityNodeInfoCompat?): Boolean {
        return supportsAnyAction(
            node,
            AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT,
            AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT
        )
    }

    /**
     * Determines whether or not the given node contains native web content (and not ChromeVox).
     *
     * @param node The node to evaluate
     * @return `true` if the node contains native web content, `false` otherwise
     */
    fun hasNativeWebContent(node: AccessibilityNodeInfoCompat?): Boolean {
        if (node == null) {
            return false
        }

        if (!supportsWebActions(node)) {
            return false
        }

        // ChromeVox does not have sub elements, so if the parent element also has web content
        // this cannot be ChromeVox.
        val parent = node.parent
        if (supportsWebActions(parent)) {
            parent?.recycle()
            return true
        }

        parent?.recycle()

        // ChromeVox never has child elements
        return node.childCount > 0
    }

    /**
     * Determines whether or not the given node contains ChromeVox content.
     *
     * @param node The node to evaluate
     * @return `true` if the node contains ChromeVox content, `false` otherwise
     */
    fun hasLegacyWebContent(node: AccessibilityNodeInfoCompat?): Boolean {
        if (node == null) {
            return false
        }

        // TODO: Need better checking for native versus legacy web content.
        // Right now Firefox is accidentally treated as legacy web content using the current
        // detection routines; the `isNodeFromFirefox` check blacklists any Firefox that supports
        // the native web actions from being treated as "legacy" content.
        // Once we have resolved this issue, remove the `isNodeFromFirefox` disjunct from the check.
        if (!supportsWebActions(node) || isNodeFromFirefox(node)) {
            return false
        }

        // ChromeVox does not have sub elements, so if the parent element also has web content
        // this cannot be ChromeVox.
        val parent = node.parent
        if (supportsWebActions(parent)) {
            parent?.recycle()

            return false
        }

        parent?.recycle()

        // ChromeVox never has child elements
        return node.childCount == 0
    }

    /**
     * @return `true` if the user has explicitly enabled injection of
     * accessibility scripts into web content.
     */
    fun isScriptInjectionEnabled(context: Context): Boolean {
        val injectionSetting: Int = Settings.Secure.getInt(
            context.contentResolver, ACCESSIBILITY_SCRIPT_INJECTION, 0
        )
        return (injectionSetting == 1)
    }

    /**
     * Returns whether the given node has navigable web content, either legacy (ChromeVox) or native
     * web content.
     *
     * @param context The parent context.
     * @param node The node to check for web content.
     * @return Whether the given node has navigable web content.
     */
    fun hasNavigableWebContent(
        context: Context, node: AccessibilityNodeInfoCompat?
    ): Boolean {
        return ((supportsWebActions(node) && isScriptInjectionEnabled(context))
                || hasNativeWebContent(node))
    }

    /**
     * Check if node is web container
     */
    fun isWebContainer(node: AccessibilityNodeInfoCompat?): Boolean {
        if (node == null) {
            return false
        }
        return hasNativeWebContent(node) || isNodeFromFirefox(node)
    }

    private fun isNodeFromFirefox(node: AccessibilityNodeInfoCompat?): Boolean {
        if (node == null) {
            return false
        }

        val packageName = if (node.packageName != null) node.packageName.toString() else ""
        return packageName.startsWith("org.mozilla.")
    }
}


fun supportsAnyAction(
    node: AccessibilityNodeInfoCompat?,
    vararg actions: Int
): Boolean {
    if (node != null) {
        val supportedActions = node.actions

        for (action in actions) {
            if ((supportedActions and action) == action) {
                return true
            }
        }
    }

    return false
}