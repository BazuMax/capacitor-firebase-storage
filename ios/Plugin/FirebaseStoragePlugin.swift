import Foundation
import Capacitor
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(FirebaseStoragePlugin)
public class FirebaseStoragePlugin: CAPPlugin {
    private let implementation = FirebaseStorageManager()

    func sendState(state: String) {
        self.notifyListeners("onUploadStateChanged", data: ["state": state])
    }

    @objc func uploadFile(_ call: CAPPluginCall) {
        let storagePath = call.getString("storagePath") ?? ""
        if (call.getBool("withImagePicker", false) == true) {
            implementation.selectImage(view: (bridge?.viewController)!) { (result, url) in
                if (!result) {
                    call.reject("User cancelled")
                    return
                }

                self.sendState(state: "uploading")
                self.implementation.uploadFile(path: storagePath, url: url!, contentType: "image/jpeg", self.resolveCallback(key: "url", call: call))
            }
        }
    }


    @objc func deleteFile(_ call: CAPPluginCall) {
        let path = call.getString("path") ?? ""
        implementation.deleteFile(path: path, resolveCallback(key: "message", call: call))
    }

    @objc func getDownloadUrl(_ call: CAPPluginCall) {
        let path = call.getString("path") ?? ""
        implementation.getDownloadUrl(path: path, resolveCallback(key: "url", call: call))
    }

    @objc func watchFileCreation(_ call: CAPPluginCall) {
        let path = call.getString("path") ?? ""
        implementation.watchFileCreation(path: path, resolveCallback(key: "url", call: call))
    }

    func resolveCallback(key: String, call: CAPPluginCall) -> (((Bool, String?) -> ())) {
        return { result, value in
            if (!result) {
                call.reject("Invalid code: 1")
                return
            }

            call.resolve([
                key: value!
            ])
        }
    }
}
