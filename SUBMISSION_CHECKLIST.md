# Plugin Submission Checklist

This checklist ensures the GBM - Group Bronzeman Mode plugin is ready for official submission.

## ✅ Pre-Submission Checklist

### 1. Plugin Metadata & Configuration

- [x] **Plugin Display Name**: "GBM - Group Bronzeman Mode" (unique, no conflicts)
- [x] **Author**: tugglicious
- [x] **Support URL**: https://github.com/tugglicious/GroupBronzeManMode
- [x] **Description**: Clear, concise description in runelite-plugin.properties
- [x] **Tags**: Relevant tags (overlay, bronzeman, group, gbm)
- [x] **Main Plugin Class**: Properly defined in runelite-plugin.properties
- [x] **Version**: 1.2 (defined in build.gradle)

### 2. Build Configuration

- [x] **settings.gradle**: Project name set to 'group-bronzeman-mode'
- [x] **build.gradle**:
  - [x] Proper dependencies (compileOnly for RuneLite client)
  - [x] Version set correctly
  - [x] Source compatibility: Java 1.8
  - [x] JAR task includes runelite-plugin.properties
  - [x] createProperties task for version file
- [x] **Builds successfully**: JAR created with all necessary files
- [x] **JAR includes**:
  - [x] Compiled .class files
  - [x] runelite-plugin.properties at root
  - [x] Resources (icons, version file)

### 3. Code Quality

- [x] **No compilation errors**: Code compiles cleanly
- [x] **No hardcoded sensitive data**: No API keys, passwords, or secrets in code
- [x] **No test/debug code**: No println, hardcoded test values
- [x] **Proper error handling**: Try-catch blocks where appropriate
- [x] **Null safety**: Null checks for critical operations
- [x] **Thread safety**: Proper use of clientThread.invoke() for client operations
- [x] **Resource cleanup**: shutDown() properly cleans up resources
- [x] **Logging**: Uses log.info/warn/error appropriately (not System.out)

### 4. Licensing & Attribution

- [x] **LICENSE file**: BSD 2-Clause License included
- [x] **Copyright**: Credits both tugglicious (2025) and Panter (2020, Another Bronzeman Mode)
- [x] **Code headers**: All major files have copyright headers
- [x] **Attribution**: README credits original creators
  - [x] CodePanter (Another Bronzeman Mode)
  - [x] Sethrem (original Bronzeman Mode)
  - [x] Dekvall (code improvements)
  - [x] Robin Withes (features)
  - [x] GUDI/Mod Ronan (concept)

### 5. Documentation

- [x] **README.md**: Complete overview of plugin
  - [x] Description of game mode
  - [x] Feature list
  - [x] Group synchronization explanation
  - [x] Reset command warning
  - [x] Credits section
- [x] **WIKI.md**: Comprehensive user guide
  - [x] Installation instructions
  - [x] Configuration guide (every setting explained)
  - [x] Group sync setup (step-by-step)
  - [x] Commands reference
  - [x] Troubleshooting section
  - [x] FAQ
- [x] **JSONBIN_SETUP_GUIDE.md**: Detailed JSONBin.io setup instructions
- [x] **Code comments**: Complex logic is commented
- [x] **JavaDoc**: Public methods have documentation

### 6. Features & Functionality

#### Core Features
- [x] **GE restrictions**: Unlocked items only
- [x] **Item tracking**: Automatic unlock detection
- [x] **Unlock notifications**: Visual overlay
- [x] **Screenshots**: Optional auto-screenshot
- [x] **Chat messages**: Optional unlock announcements
- [x] **Commands**: !bmunlocks, !bmcount, !bmreset, !bmbackup

#### Group Features
- [x] **Cloud sync**: JSONBin.io integration
- [x] **Automatic syncing**: Configurable interval (1-60 minutes)
- [x] **Unlock attribution**: Track who unlocked what
- [x] **Offline support**: Works without internet, syncs when available
- [x] **Auto FC messages**: Friends chat announcements with message queuing
- [x] **Trading restrictions**: Only trade with group members (optional)
- [x] **Character whitelist**: Restrict plugin to specific characters
- [x] **Chat icons**: Bronzeman icons for group members

### 7. Configuration Options

All config options properly implemented:

**Character Whitelist Section**
- [x] Enable character whitelist (checkbox)
- [x] Whitelisted characters (text input, comma-separated)

**Bronzeman Names Section**
- [x] Bronzeman Names (Group Members) (text input, comma-separated)

**Screenshots Section**
- [x] Take unlock screenshots (checkbox)
- [x] Include client frame (checkbox)

**Notifications Section**
- [x] Send notification on unlock (checkbox)
- [x] Send chat message on unlock (checkbox)
- [x] Auto friends chat message (checkbox)

**Trading Section**
- [x] Allow trading (checkbox, disables restrictions)

**Commands Section**
- [x] Enable reset command (checkbox)

**Display Section**
- [x] Hide untradeables (checkbox)

**Group Synchronization Section**
- [x] Enable group synchronization (checkbox)
- [x] JSONBin API Key (text input)
- [x] JSONBin Bin ID (text input)
- [x] Group name (text input, optional)
- [x] Sync interval (number input, 1-60 minutes)
- [x] Show who unlocked items (checkbox)

### 8. Testing & Validation

- [x] **Plugin loads**: Appears in Plugin Hub when sideloaded
- [x] **Configuration panel**: All settings accessible and functional
- [x] **Core functionality**: GE restrictions work
- [x] **Unlock detection**: Items unlock automatically
- [x] **Group sync**: JSONBin integration works
  - [x] Can fetch unlocks from cloud
  - [x] Can push unlocks to cloud
  - [x] Merge strategy works (union)
  - [x] Handles network errors gracefully
- [x] **Auto FC messages**:
  - [x] Message appears in chatbox
  - [x] Multiple unlocks queue properly
  - [x] Works with Key Remapping plugin
- [x] **Character whitelist**:
  - [x] Non-whitelisted characters are disabled
  - [x] Whitelisted characters work normally
  - [x] Mid-session changes work
  - [x] Retry logic handles null names
- [x] **Trading restrictions**:
  - [x] Blocks trades with non-group members
  - [x] Allows trades with group members
  - [x] Mid-session list updates work
  - [x] Color tags handled correctly
- [x] **Commands**: All commands work (!bmunlocks, !bmbackup, !bmreset)
- [x] **No crashes**: Plugin handles errors gracefully
- [x] **No memory leaks**: Resources cleaned up properly

### 9. Security & Privacy

- [x] **No data collection**: Plugin doesn't collect user data
- [x] **No telemetry**: No analytics or tracking
- [x] **No unauthorized network requests**: Only JSONBin API if user configures it
- [x] **API keys user-provided**: No embedded API keys
- [x] **User data stays local**: Unlocks stored locally, cloud sync optional
- [x] **No malicious code**: Code reviewed for security issues

### 10. RuneLite Plugin Hub Guidelines

- [x] **Follows RuneLite coding standards**: Uses RuneLite APIs properly
- [x] **No client modifications**: Doesn't modify game client
- [x] **No unfair advantages**: Doesn't provide gameplay advantages
- [x] **No automation**: Doesn't automate gameplay
- [x] **No packet manipulation**: Doesn't interfere with game network traffic
- [x] **Uses provided APIs**: Uses RuneLite's event system, client APIs
- [x] **Proper event handling**: @Subscribe annotations, event bus
- [x] **No blocking operations**: Uses async/background threads for network
- [x] **Client thread safety**: Uses clientThread.invoke() for client operations

### 11. Known Limitations & Warnings

Documented in WIKI.md:

- [x] **Reset command limitation**: Only resets local, not cloud (documented)
- [x] **Key Remapping compatibility**: Requires extra Enter press (documented)
- [x] **JSONBin free tier limits**: 10,000 requests/month (documented)
- [x] **Offline sync limitation**: Requires internet for cloud sync (documented)
- [x] **Plugin name conflict**: Original "Group Bronzeman Mode" exists (renamed to "GBM")

### 12. Final Checks

- [x] **No placeholder text**: All text is final, no TODOs
- [x] **No broken links**: All documentation links work
- [x] **Consistent naming**: "GBM - Group Bronzeman Mode" used consistently
- [x] **Version numbers match**: build.gradle and documentation consistent
- [x] **Git repo clean**: No uncommitted changes
- [x] **All changes pushed**: Latest code on GitHub
- [x] **README links to docs**: Easy navigation for users
- [x] **Support URL valid**: GitHub repo accessible

## 🚀 Ready for Submission

All checklist items completed! The plugin is ready for:

### Option 1: RuneLite Plugin Hub Submission

**Requirements:**
- Fork the [RuneLite Plugin Hub repository](https://github.com/runelite/plugin-hub)
- Add your plugin to the repository
- Create a Pull Request
- Wait for review and approval

**Submission Files Needed:**
- Source code (all Java files)
- runelite-plugin.properties
- build.gradle
- settings.gradle
- README.md
- LICENSE
- icon.png

**Note:** The official Plugin Hub has a review process. Be prepared to:
- Answer questions from reviewers
- Make requested changes
- Wait for approval (can take days/weeks)

### Option 2: Community Distribution (Sideloading)

**Already ready for:**
- Share JAR file directly with users
- Users sideload via `.runelite/sideloaded-plugins/`
- Provide installation instructions (in WIKI.md)
- Host on GitHub Releases

**Advantages:**
- Immediate availability
- No approval process
- Full control over updates
- Direct user feedback

**Disadvantages:**
- Users must manually install
- Not in official Plugin Hub
- Manual update process

## 📝 Post-Submission Checklist

After submission (if going official route):

- [ ] Monitor Pull Request for reviewer comments
- [ ] Respond to feedback promptly
- [ ] Make requested changes if any
- [ ] Update documentation if needed
- [ ] Test any changes thoroughly
- [ ] Wait for approval notification
- [ ] Thank reviewers
- [ ] Announce to community

## 🎯 Recommendation

**For This Plugin:** Consider **Community Distribution (Option 2)** because:

1. **Complex Setup**: JSONBin.io setup might be too complex for casual users
2. **External Dependency**: Relies on third-party service (JSONBin)
3. **Niche Use Case**: Group Bronzeman Mode is a specific game mode
4. **Faster Iteration**: You can push updates immediately without review
5. **Name Conflict**: There's already a "Group Bronzeman Mode" in Plugin Hub

**Alternative:** You could submit under a different name or wait for the existing plugin to be deprecated.

## 📧 Support & Maintenance

**Post-Release:**
- Monitor GitHub Issues
- Respond to user questions
- Fix bugs promptly
- Consider feature requests
- Keep dependencies updated
- Test with RuneLite updates

---

✅ **Status: READY FOR DISTRIBUTION**

The plugin is production-ready and fully functional. All critical features tested and documented.
