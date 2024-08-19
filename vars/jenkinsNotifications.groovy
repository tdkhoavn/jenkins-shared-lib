def notify(String status, String message, String credentialsId) {
  def colors = [
      'Started': 'Accent', // blue
      'Success': 'Good', // green
      'Failure': 'Attention' // red
  ]

  if (!colors.containsKey(status)) {
      error "Invalid status: $status"
  }

  def color = colors[status]

  def title = "Jenkins Build Notification"

  withCredentials([string(credentialsId: "${credentialsId}", variable: 'webhookUrl')]) {
      def payload = [
        "type": "message",
        "attachments": [
            [
              "contentType": "application/vnd.microsoft.card.adaptive",
              "content": [
                    "\$schema": "http://adaptivecards.io/schemas/adaptive-card.json",
                    "type": "AdaptiveCard",
                    "version": "1.3",
                    "body": [
                        [
                            "type": "TextBlock",
                            "size": "Medium",
                            "weight": "Bolder",
                            "text": "${title}"
                        ],
                        [
                            "type": "ColumnSet",
                            "columns": [
                                [
                                    "type": "Column",
                                    "items": [
                                        [
                                            "type": "Image",
                                            "style": "Person",
                                            "url": "https://ftp-chi.osuosl.org/pub/jenkins/art/jenkins-logo/1024x1024/logo.png",
                                            "altText": "Jenkins",
                                            "size": "Medium"
                                        ]
                                    ],
                                    "width": "auto"
                                ],
                                [
                                    "type": "Column",
                                    "items": [
                                        [
                                            "type": "TextBlock",
                                            "weight": "Bolder",
                                            "text": "Jenkins Pipelines",
                                            "wrap": true
                                        ]
                                    ],
                                    "width": "auto"
                                ]
                            ]
                        ],
                        [
                            "type": "TextBlock",
                            "text": "${message}",
                            "wrap": true
                        ],
                        [
                            "type": "ColumnSet",
                            "columns": [
                                [
                                    "type": "Column",
                                    "width": "auto",
                                    "items": [
                                        [
                                            "type": "TextBlock",
                                            "text": "Build Number:",
                                            "weight": "Bolder",
                                            "color": "Accent"
                                        ]
                                    ]
                                ],
                                [
                                    "type": "Column",
                                    "width": "auto",
                                    "items": [
                                        [
                                            "type": "TextBlock",
                                            "text": "${env.BUILD_NUMBER}",
                                            "wrap": true
                                        ],
                                    ]
                                ]
                            ]
                        ],
                        [
                            "type": "ColumnSet",
                            "columns": [
                                [
                                    "type": "Column",
                                    "width": "auto",
                                    "items": [
                                        [
                                            "type": "TextBlock",
                                            "text": "Build Status:",
                                            "weight": "Bolder",
                                            "color": "Accent"
                                        ]
                                    ]
                                ],
                                [
                                    "type": "Column",
                                    "width": "auto",
                                    "items": [
                                        [
                                            "type": "TextBlock",
                                            "weight": "Bolder",
                                            "color": "${color}",
                                            "text": "${status}",
                                            "wrap": true
                                        ],
                                    ]
                                ]
                            ]
                        ],
                        [
                            "type": "TextBlock",
                            "text": "<at>${tagName}</at>",
                            "wrap": true
                        ]
                    ],
                    "actions": [
                        [
                            "type": "Action.OpenUrl",
                            "title": "Open Build in Jenkins",
                            "url": "${env.BUILD_URL}"
                        ]
                    ],
                    "msteams": [
                        "entities": [
                            [
                                "type": "mention",
                                "text": "<at>${tagName}</at>",
                                "mentioned": [
                                    "id": "${tagId}",
                                    "name": "${tagName}"
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
      ]

      def response = httpRequest(
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        requestBody: groovy.json.JsonOutput.toJson(payload),
        url: webhookUrl
      )

      echo "Response: ${response}"
  }
}
