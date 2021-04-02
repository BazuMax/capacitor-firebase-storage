import Foundation
import UIKit
import FirebaseCore
import FirebaseStorage

@objc(FirebaseStorageManager) public class FirebaseStorageManager: NSObject {
    let storageRef = Storage.storage().reference()

    @objc public func uploadFileData(path: String, data: Data, contentType: String, _ callback: @escaping ((Bool, String?) -> ())) {
        let fileRef = storageRef.child(path)

        let metadata = StorageMetadata()
        metadata.contentType = contentType
        fileRef.putData(data, metadata: metadata) { (metadata, error) in
            if ((error) != nil) {
                callback(false, error?.localizedDescription)
                return
            }

            fileRef.downloadURL { (url, error) in
                callback(true, url?.absoluteString)
            }
        }
    }

    @objc public func uploadFile(path: String, url: URL, contentType: String, _ callback: @escaping ((Bool, String?) -> ())) {
        let fileRef = storageRef.child(path)

        fileRef.putFile(from: url, metadata: nil) { (metadata, error) in
            if ((error) != nil) {
                callback(false, error?.localizedDescription)
                return
            }

            fileRef.downloadURL { (url, error) in
                callback(true, url?.absoluteString)
            }
        }
    }

    @objc public func selectImage(view: UIViewController, _ callback: @escaping ((Bool, URL?) -> ())) {
        DispatchQueue.main.async {
            ImagePickerManager().pickImage(view, callback)
        }
    }

    @objc public func deleteFile(path: String, _ callback: @escaping ((Bool, String?) -> ())) {
        storageRef.child(path).delete { (error) in
            if ((error) != nil) {
                callback(false, error?.localizedDescription)
                return
            }
            callback(true, "success")
        }
    }

    @objc public func getDownloadUrl(path: String, _ callback: @escaping ((Bool, String?) -> ())) {
        storageRef.child(path).downloadURL { (url, error) in
            if ((error) != nil) {
                callback(false, error?.localizedDescription)
                return
            }
            callback(true, url?.absoluteString)
        }
    }

    @objc public func watchFileCreation(path: String, tryIndex: Int = 0, _ callback: @escaping ((Bool, String?) -> ())) {
        if (tryIndex > 10) {
            callback(false, "timeout")
            return
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { [self] in
            storageRef.child(path).downloadURL { (url, error) in
                if ((error) != nil) {
                    watchFileCreation(path: path, tryIndex: tryIndex+1, callback)
                    return
                }
                callback(true, url?.absoluteString)
            }
        }

    }


}
